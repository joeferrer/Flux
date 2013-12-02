
import java.io.*;
import java.net.*;
//import java.util.*;

public class MyConnection {
	Socket socket;
	
	OutputStream a;
	OutputStreamWriter b;
	PrintWriter c;
	
	InputStream d;
	InputStreamReader f;
	BufferedReader g;
	
	String msg;
	
	public MyConnection (Socket s) {
		socket = s;
		try {
			a = socket.getOutputStream();
			b = new OutputStreamWriter(a);
			c = new PrintWriter(b);
			d = socket.getInputStream();
			f = new InputStreamReader(d);
			g = new BufferedReader(f);
			
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public boolean sendMessage(String msg) {
		try {	
			c.println(msg);
			c.flush();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}			
	}
	public String getMessage() {
		try {	
			msg = g.readLine();
			return msg;
		}catch(Exception e) {
			e.printStackTrace();
			return "getMessage() failed";
		}	
	}
}
