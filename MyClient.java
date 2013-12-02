import java.io.*;
import java.net.*;

public class MyClient {
	public static void main(String args[]) {

		Socket socket;
		MyConnection conn;
		try {
			socket = new Socket("127.0.0.1",8888);
			conn = new MyConnection(socket);
			
			//TSender ts = new TSender(socket,conn);
			
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}	
	}
}