
import javax.swing.*;
import java.io.*;
import java.net.*;
public class TSender extends Thread {
	Socket socket;
	String input;
	MyConnection conn;
	
	public TSender(Socket x, MyConnection y) {
		socket = x;
		conn = y;
		input = "";
	}
	public void run() {
		while(true) {
			try {
				while(input.equals("")){
					/*System.out.println("yo")*/;
					Thread.sleep(5);
				}
				//System.out.println("I got something...");
				conn.sendMessage(input);
				//if(input.toUpperCase().equals("/QUIT")) break;
				input = "";
			}catch (Exception e) {
				break;
			}
		}	
	}	
}
