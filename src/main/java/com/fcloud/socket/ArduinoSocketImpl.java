/**
 * @(#) ArduinoSocketImpl.java Created on 2014-3-24
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */
package com.fcloud.socket;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.fcloud.socket.ArduinoServer.ArduinoSocketWorker;

/**
 * The class <code>ArduinoSocketImpl</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class ArduinoSocketImpl implements ArduinoSocket {

	Logger logger = Logger.getLogger(ArduinoSocketImpl.class);

	enum Conn_Statu {
		HAND, CONNETED, DISCONNET
	}

	public SelectionKey key;

	public ByteChannel channel;
	/**
	 * Queue of buffers that need to be sent to the client.
	 */
	public final BlockingQueue<ByteBuffer> outQueue;
	/**
	 * Queue of buffers that need to be processed
	 */
	public final BlockingQueue<ByteBuffer> inQueue;

	public volatile ArduinoSocketWorker workerThread;

	public ArduinoSocketListener listener;

	public volatile Conn_Statu statu = Conn_Statu.DISCONNET;

	public String name;

	private StringBuilder buffer = new StringBuilder();

	private Queue<String> messages = new LinkedBlockingQueue<String>();

	protected SocketAddress address;

	/**
	 * 
	 */
	public ArduinoSocketImpl(ArduinoSocketListener listener) {
		super();
		this.outQueue = new LinkedBlockingQueue<ByteBuffer>();
		inQueue = new LinkedBlockingQueue<ByteBuffer>();
		this.listener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onOpen()
	 */
	@Override
	public void onOpen(ArduinoSocket arduinoSocket) {
		listener.onAduinoSocketOpen(this);
		statu = Conn_Statu.CONNETED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onClose()
	 */
	@Override
	public void onClose() {
		this.outQueue.clear();
		listener.onAduinoSocketClose(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onMessage(java.lang.String)
	 */
	@Override
	public void onMessage(ArduinoSocket arduinoSocket, String message) {
		putBuffer(message);
		while (this.messages.peek() != null) {
			final String msg = this.messages.poll();
			if (this.statu != Conn_Statu.HAND)
				if (isHand(msg)) {
					this.statu = Conn_Statu.HAND;
					try {
						sendLine("200");
						onOpen(this);
					} catch (IOException e) {
						logger.error("onOpen error", e);
					}
					
				} else {
					listener.onAduinoSocketMessae(this, msg);
				}

		}
	}

	private void putBuffer(String message) {
		boolean isR = false;
		for (char c : message.toCharArray()) {
			if (c == '\r') {
				isR = true;
			} else if (isR && c == '\n') {
				this.messages.add(buffer.toString());
				this.buffer = new StringBuilder();
			} else {
				buffer.append(c);
			}
		}
	}

	private boolean isHand(String message) {
		if (null != message) {
			Pattern pattern = Pattern.compile("Arduino \\w+");
			final boolean m = pattern.matcher(message).find();
			if (m) {
				setName(message);
				return m;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#onError(java.lang.Exception)
	 */
	@Override
	public void onError(ArduinoSocket arduinoSocket, Exception e) {
		listener.onAduinoSocketError(this, e);
		statu = Conn_Statu.DISCONNET;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#isConneted()
	 */
	@Override
	public boolean isConneted() {
		return this.statu == Conn_Statu.CONNETED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#close()
	 */
	@Override
	public void close() {
		if (statu != Conn_Statu.DISCONNET) {
			statu = Conn_Statu.DISCONNET;
			key.cancel();
			try {
				channel.close();
				this.outQueue.clear();
			} catch (IOException e) {
				onError(this, e);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#send(java.lang.String)
	 */
	@Override
	public boolean send(String message) throws IOException {
		// logger.debug("send:" + message);
		if (isConneted() || statu == Conn_Statu.HAND) {
			final byte[] bs = message.getBytes("UTF-8");
			ByteBuffer buffer = ByteBuffer.wrap(bs);
			buffer.put(bs);
			buffer.flip();
			outQueue.add(buffer);
			key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			listener.onWriteDemand(this);
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#sendLine(java.lang.String)
	 */
	@Override
	public boolean sendLine(String message) throws IOException {
		return send(message + "\r\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#getName()
	 */
	@Override
	public String getName() {
		return this.name;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#setName(java.lang.String)
	 */
	@Override
	public void setName(String name) {
		this.name = name;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#getSocketAddress()
	 */
	@Override
	public SocketAddress getSocketAddress() {
		return address;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fcloud.socket.ArduinoSocket#setSocketAddress(java.net.
	 * InetSocketAddress)
	 */
	@Override
	public void setSocketAddress(SocketAddress address) {
		this.address = address;

	}

}
