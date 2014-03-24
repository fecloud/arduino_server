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
//			onError(e);
			return;
		}

		try {
			while (!selectorthread.isInterrupted()) {
				SelectionKey key = null;
				ArduinoSocketImpl conn = null;
				try {
					selector.select();
					Set<SelectionKey> keys = selector.selectedKeys();
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
							w.channel = channel;
							i.remove();
							continue;
						}

						if (key.isReadable()) {
							conn = (ArduinoSocketImpl) key.attachment();
							ByteBuffer buf = createBuffer();
							if (SocketChannelIOHelper.read(buf, conn, conn.channel)) {
								if (buf.hasRemaining()) {
									conn.inQueue.put(buf);
									queue(conn);
								}
							}
						}

						if (key.isWritable()) {
							conn = (ArduinoSocketImpl) key.attachment();
							try {

								SocketChannelIOHelper.batch(conn, conn.channel);

							} catch (IOException e) {
								throw e;
							}
						}
					}
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

	private void queue(ArduinoSocketImpl ws) throws InterruptedException {
		if (ws.workerThread == null) {
			ws.workerThread = workers.get(queueinvokes % workers.size());
			queueinvokes++;
		}
		ws.workerThread.put(ws);
	}

	public ByteBuffer createBuffer() {
		return ByteBuffer.allocate(Short.MAX_VALUE);
	}

	public void onAduinoSocketOpen(ArduinoSocket conn) {
		onOpen(conn);
	}

	public void onAduinoSocketError(ArduinoSocket conn, Exception e) {
		onError(conn,e);
	}

	public void onAduinoSocketClose(ArduinoSocket conn) {
		onClose();
	}

	public void onAduinoSocketMessae(ArduinoSocket conn, String message) {
		onMessage(conn,message);
	}

	protected abstract void onOpen(ArduinoSocket arduinoSocket);

	protected abstract void onClose();

	protected abstract void onMessage(ArduinoSocket arduinoSocket,String message);

	protected abstract void onError(ArduinoSocket arduinoSocket,Exception exception);

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
					}
				}
			} catch (InterruptedException e) {
			} catch (RuntimeException e) {
				ws.close();
			}
		}

	}

}
