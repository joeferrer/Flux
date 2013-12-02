
import java.io.*;
import java.net.*;
import java.util.*;
public class SExtension implements Runnable {
	String read;;
	LinkedList <ID> id_list;
	int id_num;
	
	public SExtension(int c) {
		LinkedList <ID> id_list = new LinkedList <ID> ();
		id_num = c;
	}
	public void run() {
		try {
			while(true) {
				read = id_list.get(id_num).conn.getMessage();
				//if(read.equals("0;0")){
					for(int i=0;i<id_list.size();i++) {
						if(i!=id_num) {
							id_list.get(i).conn.sendMessage(read);
							//System.out.println("id="+id_num);
						}
					}
				//}
			}	
		}
		catch(Exception e) {}
	}
	

}
