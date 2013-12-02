
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import java.io.*;
import java.net.*; 

public class SlickBasicGame extends BasicGame{
 
    Image plane = null;
    Image plane1 = null;
    Image land = null;
    float x = 400;
    float y = 300;
    float scale = 1;
    
    float x1 = 400;
    float y1 = 300;
    
    Socket socket;
    MyConnection conn;
    TSender ts;
    TReceiver tr;
    
    
    StringWrapper sw = new StringWrapper("400;300");
    
    boolean isW = false;  
    boolean W_Switch = false;
    
    
    public SlickBasicGame()
    {
        super("Slick2D Path2Glory - SlickBasicGame");
        try{
        	socket = new Socket("127.0.0.1",8888);
			conn = new MyConnection(socket);
			
			ts = new TSender(socket,conn);
			tr = new TReceiver(socket,conn,sw);
			
			ts.start();
			tr.start();
        }catch(Exception e){}
        
    }
 
    @Override
    public void init(GameContainer gc)	
			throws SlickException {
        plane = new Image("data/sprite-1.png");
        plane1 = new Image("data/sprite-2.png");
        land = new Image("data/land.jpg");
        
    }
 
    @Override
    public void update(GameContainer gc, int delta)
			throws SlickException
    {
        Input input = gc.getInput();
        

         if(sw.getString().indexOf(';') != -1)  { 
        	 x1 = Float.parseFloat(sw.getString().split(";")[0]); 
        	 y1 = Float.parseFloat(sw.getString().split(";")[1]); 
         }
        
        
        if(input.isKeyDown(Input.KEY_A))
        {
        	plane.rotate(-0.2f * delta);
        }
 
        if(input.isKeyDown(Input.KEY_D))
        {
            plane.rotate(0.2f * delta);
        }
 
        if(input.isKeyDown(Input.KEY_W))
        {
            float hip = 0.2f * delta;
 
            float rotation = plane.getRotation();
 
            x+= hip * Math.sin(Math.toRadians(rotation));
            y-= hip * Math.cos(Math.toRadians(rotation));
           
        }
        
        
        ts.input = Float.toString(x)+";"+Float.toString(y);
        
        if(input.isKeyDown(Input.KEY_S))
        {
            float hip = 0.2f * delta;
 
            float rotation = plane.getRotation();
 
            x-= hip * Math.sin(Math.toRadians(rotation));
            y+= hip * Math.cos(Math.toRadians(rotation));
        }	        
    }

    public void render(GameContainer gc, Graphics g)
			throws SlickException
    {
    	
        land.draw(0,0);
        
        plane.draw(Math.abs(x+800)%800, Math.abs(y+600)%600, scale);
       
        /*if(sw.getString().indexOf(';') != -1){
        	x1 = Float.parseFloat(sw.getString().split(";")[0]);
        	y1 = Float.parseFloat(sw.getString().split(";")[1]);
        }*/
        
        plane1.draw(Math.abs(x1+800)%800, Math.abs(y1+600)%600, scale);
        
    }
 
    public static void main(String[] args)
			throws SlickException
    {
    	SlickBasicGame sbg = new SlickBasicGame();
    	
         AppGameContainer app =
			new AppGameContainer(sbg);
         
         app.setDisplayMode(800, 600, false);
     
         app.start();
       
         
    }
}
