import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

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
	public ArduinoClient(Socket socket) {
		super();
		this.socket = socket;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		Socket socket = new Socket("111.206.45.12", 30156);
		socket.setSoTimeout(10000);
		OutputStream out = socket.getOutputStream();
		out.write("Arduino uno\r\n".getBytes("UTF-8"));
		out.flush();
		
		new ArduinoClient(socket).start();
		
		while (true) {
			Thread.sleep(2000);
			out.write(new String("" + System.currentTimeMillis() + "\r\n").getBytes("UTF-8"));
			out.flush();
			
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		InputStream in;
		try {
			in = socket.getInputStream();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while(null != (line = reader.readLine())){
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
