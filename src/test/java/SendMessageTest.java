import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class SendMessageTest {

	/**
	 * @param args
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public static void main(String[] args)  {
		while(true){
			try {
				sendMsg();
				Thread.sleep(3000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	
	public static void sendMsg() throws IOException, InterruptedException{
		URL url = new URL("http://fecloud.duapp.com/arduino/?t=3&v={%22device%22:%22Arduino%20uno%22,%22message%22:%22ddddddddddfdddddddddddddfdddddddddfdddddddddfddddddddddfdfrddd%22}");
		 HttpURLConnection connection= (HttpURLConnection) url.openConnection();
		 connection.setDoInput(true);
		 connection.connect();
		 InputStream in = connection.getInputStream();
		 BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		 String line = null;
		 while(null != (line = reader.readLine())){
			 System.out.println(line);
		 }
		 Thread.sleep(2000);
		 connection.disconnect();
		 
		 
	}

}
