/**
 * @(#) ArduinoServer.java Created on 2014-3-19
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.fcloud.utils.SocketChannelIOHelper;

/**
 * The class <code>ArduinoServer</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public abstract class ArduinoServer implements Runnable, ArduinoSocketListener {

	Logger logger = Logger.getLogger(ArduinoServer.class);

	public static int DECODERS = Runtime.getRuntime().availableProcessors();

	private int port;

	private List<ArduinoSocketWorker> workers;

	protected ServerSocketChannel serverSocketChannel;

	protected Selector selector;

	private Thread selectorthread;

	private volatile AtomicBoolean isclosed = new AtomicBoolean(false);
	private int queueinvokes = 0;

	protected List<ArduinoSocket> conntions = new ArrayList<ArduinoSocket>();

	private BlockingQueue<ByteBuffer> buffers;

	private AtomicInteger queuesize = new AtomicInteger(0);

	public ArduinoServer(int port) {
		this(port, DECODERS);
	}

	/**
	 * @param port
	 */
	public ArduinoServer(int port, int decodercount) {
		super();
		this.port = port;

		workers = new ArrayList<ArduinoSocketWorker>(decodercount);
		buffers = new LinkedBlockingQueue<ByteBuffer>();
		for (int i = 0; i < decodercount; i++) {
			ArduinoSocketWorker ex = new ArduinoSocketWorker();
			workers.add(ex);
			new Thread(ex).start();
		}
	}

	public void start() {
		if (selectorthread != null)
			throw new IllegalStateException(getClass().getName() + " can only be started once.");
		new Thread(this).start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		synchronized (this) {
			if (selectorthread != null)
				throw new IllegalStateException(getClass().getName() + " can only be started once.");
			selectorthread = Thread.currentThread();
			if (isclosed.get()) {
				return;
			}
		}
		selectorthread.setName("ArduinoServerSelector" + selectorthread.getId());
		try {
			serverSocketChannel = ServerSocketChannel.open();
			serverSocketChannel.configureBlocking(false);
			ServerSocket serverSocket = serverSocketChannel.socket();
			serverSocket.bind(new InetSocketAddress(port));
			logger.debug("bind port:" + port);
			selector = Selector.open();
			serverSocketChannel.register(selector, serverSocketChannel.validOps());
		} catch (IOException e) {
			logger.error("", e);
			// onError(e);
			return;
		}

		try {
			while (!selectorthread.isInterrupted()) {
				SelectionKey key = null;
				ArduinoSocketImpl conn = null;
				try {
					selector.select();

					Set<SelectionKey> keys = selector.selectedKeys();
					System.err.println("selector.select()" + keys.size());
					Iterator<SelectionKey> i = keys.iterator();
					while (i.hasNext()) {
						key = i.next();
						if (!key.isValid()) {
							continue;
						}

						if (key.isAcceptable()) {
							// 创建新连接

							SocketChannel channel = serverSocketChannel.accept();
							channel.configureBlocking(false);
							ArduinoSocketImpl w = new ArduinoSocketImpl(this);
							w.key = channel.register(selector, SelectionKey.OP_READ, w);
							w.setSocketAddress(channel.socket().getRemoteSocketAddress());
							w.channel = channel;
							i.remove();
							allocateBuffers(w);
							continue;
						}

						if (key.isReadable()) {
							conn = (ArduinoSocketImpl) key.attachment();
							ByteBuffer buf = takeBuffer();
							try {
								if (SocketChannelIOHelper.read(buf, conn, conn.channel)) {
									if (buf.hasRemaining()) {
										conn.inQueue.put(buf);
										queue(conn);
										i.remove();
									}
								} else {
									pushBuffer(buf);
								}
							} catch (IOException e) {
								pushBuffer(buf);
								throw e;
							}

						}

						if (key.isWritable()) {
							conn = (ArduinoSocketImpl) key.attachment();
							if (SocketChannelIOHelper.batch(conn, conn.channel)) {
								if (key.isValid())
									key.interestOps(SelectionKey.OP_READ);
							}
							i.remove();
						}
					}
				} catch (CancelledKeyException e) {
					// an other thread may cancel the key
				} catch (ClosedByInterruptException e) {

				} catch (IOException e) {
					if (key != null)
						key.cancel();
					handleIOException(key, conn, e);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public void stop() throws InterruptedException {
		if (!isclosed.compareAndSet(false, true)) { // this also makes sure that
			return;
		}

		for (ArduinoSocket ws : conntions) {
			ws.close();
		}
		synchronized (this) {
			if (selectorthread != null) {
				if (Thread.currentThread() != selectorthread) {

				}
				if (selectorthread != Thread.currentThread()) {
					if (conntions.size() > 0)
						selectorthread.join(0);// isclosed will tell the
					selectorthread.interrupt();// in case the selectorthread did
					selectorthread.join();
				}
			}
		}
	}

	private void handleIOException(SelectionKey key, ArduinoSocket conn, IOException ex) {
		// onWebsocketError( conn, ex );// conn may be null here
		if (conn != null) {
			conn.onClose();
		} else if (key != null) {
			SelectableChannel channel = key.channel();
			if (channel != null && channel.isOpen()) {
				try {
					channel.close();
				} catch (IOException e) {
					// there is nothing that must be done here
				}
				// if( WebSocketImpl.DEBUG )
				System.out.println("Connection closed because of" + ex);
			}
		}
	}

	protected void allocateBuffers(ArduinoSocket c) throws InterruptedException {
		if (queuesize.get() >= 2 * workers.size() + 1) {
			return;
		}
		queuesize.incrementAndGet();
		buffers.put(createBuffer());
	}

	public ByteBuffer createBuffer() {
		return ByteBuffer.allocate(512);
	}

	private void queue(ArduinoSocketImpl ws) throws InterruptedException {
		if (ws.workerThread == null) {
			ws.workerThread = workers.get(queueinvokes % workers.size());
			queueinvokes++;
		}
		ws.workerThread.put(ws);
	}

	private ByteBuffer takeBuffer() throws InterruptedException {
		return buffers.take();
	}

	private void pushBuffer(ByteBuffer buf) throws InterruptedException {
		if (buffers.size() > queuesize.intValue())
			return;
		buffers.put(buf);
	}

	public void onAduinoSocketOpen(ArduinoSocket conn) {
		final ArduinoSocket arduinoSocket = getOnLine(conn.getName());
		if (null != arduinoSocket) {
			arduinoSocket.close();
		}
		conntions.add(conn);
		onOpen(conn);
	}

	public void onAduinoSocketError(ArduinoSocket conn, Exception e) {
		onError(conn, e);
		conntions.remove(conn);
	}

	public void onAduinoSocketClose(ArduinoSocket conn) {
		onClose();
		conntions.remove(conn);
	}

	public void onAduinoSocketMessae(ArduinoSocket conn, String message) {
		onMessage(conn, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fcloud.socket.ArduinoSocketListener#onWriteDemand(com.fcloud.socket
	 * .ArduinoSocket)
	 */
	@Override
	public void onWriteDemand(ArduinoSocket conn) {
		selector.wakeup();
	}

	protected abstract void onOpen(ArduinoSocket arduinoSocket);

	protected abstract void onClose();

	protected abstract void onMessage(ArduinoSocket arduinoSocket, String message);

	protected abstract void onError(ArduinoSocket arduinoSocket, Exception exception);

	public class ArduinoSocketWorker implements Runnable {

		private BlockingQueue<ArduinoSocketImpl> iqueue;

		public ArduinoSocketWorker() {
			iqueue = new LinkedBlockingQueue<ArduinoSocketImpl>();

		}

		public void put(ArduinoSocketImpl ws) throws InterruptedException {
			iqueue.put(ws);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			Thread.currentThread().setName("ArduinoSocketWorker-" + Thread.currentThread().getId());

			ArduinoSocketImpl ws = null;
			try {
				while (true) {
					ByteBuffer buf = null;
					ws = iqueue.take();
					buf = ws.inQueue.poll();
					assert (buf != null);
					try {
						ws.onMessage(ws, new String(buf.array(), buf.position(), buf.remaining(),
								"UTF-8"));
					} catch (Exception e) {
						ws.close();
					} finally {
						pushBuffer(buf);
					}
				}
			} catch (InterruptedException e) {
			} catch (RuntimeException e) {
				ws.close();
			}
		}

	}

	/**
	 * @return the conntions
	 */
	public List<ArduinoSocket> getConntions() {
		return conntions;
	}

	/**
	 * 在线列表是否有相同的客户端
	 * 
	 * @param name
	 * @return
	 */
	public ArduinoSocket getOnLine(String name) {
		for (ArduinoSocket arduinoSocket : conntions) {
			if (arduinoSocket.getName().equals(name)) {
				return arduinoSocket;
			}
		}
		return null;
	}

}
