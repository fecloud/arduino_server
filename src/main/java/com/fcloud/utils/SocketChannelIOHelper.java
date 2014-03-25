package com.fcloud.utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import com.fcloud.socket.ArduinoSocketImpl;

public class SocketChannelIOHelper {

	public static boolean read(final ByteBuffer buf, ArduinoSocketImpl ws, ByteChannel channel)
			throws IOException {
		buf.clear();
		int read = channel.read(buf);
		buf.flip();

		if (read == -1) {
			// ws.eot();
			return false;
		}
		return read != 0;
	}

	// /**
	// * @see WrappedByteChannel#readMore(ByteBuffer)
	// * @return returns whether there is more data left which can be obtained
	// via {@link #readMore(ByteBuffer, WebSocketImpl, WrappedByteChannel)}
	// **/
	// public static boolean readMore( final ByteBuffer buf, ArduinoSocketImpl
	// ws, WrappedByteChannel channel ) throws IOException {
	// buf.clear();
	// int read = channel.readMore( buf );
	// buf.flip();
	//
	// if( read == -1 ) {
	// ws.eot();
	// return false;
	// }
	// return channel.isNeedRead();
	// }
	//
	/** Returns whether the whole outQueue has been flushed */
	public static boolean batch(ArduinoSocketImpl ws, ByteChannel sockchannel) throws IOException {
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
