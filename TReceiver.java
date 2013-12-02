
import java.io.*;
import java.net.*;
import java.util.*;

public class TReceiver extends Thread {
	Socket socket;
	StringWrapper received;
	MyConnection conn;
	String[] words;
	String msg;
	
	public TReceiver(Socket x, MyConnection y,StringWrapper sw) {
		socket = x;
		conn = y;
		received = sw;
	}
	public void run() {
		while(true) {
			try {
				received.setString (conn.getMessage());
			}catch(Exception e) {
				break;
			}
		}	
	}
}
