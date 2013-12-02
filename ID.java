import java.io.*;
import java.net.*;
import java.util.*;

public class ID {
	String scrname;
	String status = "Available";
	Socket socket;
	MyConnection conn;
	int activity;
	public ID(Socket x,String d) {
		socket = x;	
		System.out.println("Server: " + socket.getInetAddress() + " connected!");
		conn = new MyConnection(socket);
		scrname = d;
		activity = 1;
	}
}	