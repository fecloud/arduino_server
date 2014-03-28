package com.fcloud.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import com.fcloud.socket.ArduinoSocketImpl;

public class SocketChannelIOHelper {

	public static boolean read(final ByteBuffer buf, ArduinoSocketImpl ws, ByteChannel channel)
			throws IOException {
		org.apache.log4j.Logger.getLogger(SocketChannelIOHelper.class).debug("read");
		buf.clear();
		int read = channel.read(buf);
		buf.flip();

		if (read == -1) {
			// ws.eot();
			return false;
		}
		return read != 0;
	}

	
	public static boolean batch(ArduinoSocketImpl ws, ByteChannel sockchannel) throws IOException {

		org.apache.log4j.Logger.getLogger(SocketChannelIOHelper.class).debug("batch");
		
		ByteBuffer buffer = ws.outQueue.peek();
		if (null != buffer) {
			do {
				sockchannel.write(buffer);
				ws.outQueue.poll();
				buffer = ws.outQueue.peek();
			} while (buffer != null);

			return true;

		}
		return false;
	}
}
