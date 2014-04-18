import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @(#) ArduinoClient.java Created on 2014-3-19
 *
 * Copyright © 2013 深圳企业云科技有限公司  版权所有
 */

/**
 * The class <code>ArduinoClient</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class ArduinoClient extends Thread {

	private Socket socket;

	/**
	 * @param socket
	 */
	public ArduinoClient() {
		super();
	}

	public void pre() throws UnknownHostException, IOException {
		socket = new Socket("127.0.0.1", 30156);
		socket.setSoTimeout(10000);
		OutputStream out = socket.getOutputStream();
		out.write("Arduino uno\r\n".getBytes("UTF-8"));
		out.flush();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		while (true) {
			try {
				ArduinoClient arduinoClient = new ArduinoClient();
				arduinoClient.pre();
				arduinoClient.start();
				OutputStream out = null;
				while (true) {
					Thread.sleep(1000);
					out = arduinoClient.socket.getOutputStream();
					out.write(new String("" + System.currentTimeMillis() + "\r\n")
							.getBytes("UTF-8"));
					out.flush();

				}
			} catch (Exception e) {
				System.err.println("disconnet");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		InputStream in;
		try {
			in = socket.getInputStream();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while (null != (line = reader.readLine())) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
