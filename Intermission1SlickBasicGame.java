import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.tiled.TiledMap;

import java.io.*;
import java.net.*;
import java.util.Random;
import java.util.Date;
import java.util.ArrayList;
import javax.swing.*;

import org.newdawn.slick.Sound;
 
public class Intermission1SlickBasicGame extends BasicGame{
	
	final float epsilon = .008f;
	private static final int SIZE = 32;	
	private static final int LIMIT_TO_WIN = 10;
	
	final int p0STATION_X = 11*SIZE;
	final int p0STATION_Y = 11*SIZE;
	
	final int p1STATION_X = 13*SIZE;
	final int p1STATION_Y = 9*SIZE;
	
	final String mapfile = "data/map2C.tmx";
	final int maxChargeOnLevel = 10;
	final int sampleSize = 80;
	final int targetNumber = 40;  
	
	
	int id,cc,yourCharge,lastChargeCollidedWith = -1,f0_drawer=0,f0time=0,f1_drawer=0,f1time=0;
	int lastChargeCollidedByOpponent = -1;
	int winner = -1;
	
    Entity plane_0 = null;
    Entity plane_1 = null;
    Entity land = null;

    TiledMap map1 = null;
    ArrayList <Rectangle> blockedRects = null;   
    ArrayList <Rectangle> holeList = null;
    
    ArrayList <Rectangle> chargeListSource = null;
    int sourceListSize = 1000;   
    boolean flashScreen = false;
    
    int emptySlots;
    
    
    Date date = new Date();
    Random chargeRandomizer = null;
    Rectangle [] chargeList = null;
    Rectangle cstation_r0, cstation_r1;
    Circle flux_r0,flux_r1;    
    Image cstation_i0, cstation_i1;
   
    Image light = null;
    Image lightSmall = null;
    Image black = null;
  
    Sound pickupCharge = null;
    Sound chargeStation_s = null;
    Sound pickupRespawn_s = null;
    Sound flux_s = null;

    Sound ambience_s = null;
    Sound distanceSound_s = null;
    
    Socket socket;
    MyConnection conn;
    TSender ts;
    TReceiver tr;
    StringWrapper sw = new StringWrapper("");
    
    
    
    public Intermission1SlickBasicGame(String ip)
    {
        super("Flux");
        try{
        	socket = new Socket(ip,8888);
			conn = new MyConnection(socket);
			
			id = Integer.parseInt(conn.getMessage());
			System.out.println("Your id is " + Integer.toString(id));
			
			ts = new TSender(socket,conn);
			tr = new TReceiver(socket,conn,sw);
			
			ts.start();
			tr.start();
        }catch(Exception e){}
    }
 
    @Override
    public void init(GameContainer gc)
			throws SlickException {
    	
    	chargeList = new Rectangle [maxChargeOnLevel];
    	
    	chargeRandomizer = new Random();
    	chargeRandomizer.setSeed(/*date.getTime()*/10);
    	
    	
        map1 = new TiledMap(mapfile);
        //blocked1 = new boolean[map1.getWidth()][map1.getHeight()];
        blockedRects = new ArrayList<Rectangle>();
        holeList = new ArrayList<Rectangle>();
        
        chargeListSource = new ArrayList<Rectangle>(1000);
        
        //light
        light = new Image("/data/light.png");
        lightSmall = new Image("/data/light-small.png");
        black = new Image("/data/black.png");
        
        //sounds
        pickupCharge = new Sound("/data/sfx/powerup-1.wav");
        chargeStation_s = new Sound("/data/sfx/charge-station.wav");
        pickupRespawn_s = new Sound("/data/sfx/respawn-1.wav");
        flux_s = new Sound("/data/sfx/flux-3.wav");
        
        ambience_s = new Sound("/data/bgm/Open Space Wind2.wav");
        distanceSound_s = new Sound("/data/bgm/grav_test_piano.wav");
        
        emptySlots = chargeList.length;
        
        int blockedCount = 0;
        int xAxis;
        int yAxis;
        
        // initialization of walls
        for (xAxis=0;xAxis<map1.getWidth(); xAxis++)
        {
                 for (yAxis=0;yAxis<map1.getHeight(); yAxis++)
                 {
                     int tileID = map1.getTileId(xAxis, yAxis, 0);
                     String value = map1.getTileProperty(tileID, "blocked", "false");
                     String isHole = map1.getTileProperty(tileID, "hole", "false");
                     if ("true".equals(value))
                     {
                         //blocked1[xAxis][yAxis] = true;
                         blockedRects.add(new Rectangle(xAxis*SIZE,yAxis*SIZE,SIZE,SIZE));
                         blockedCount += 1;
                         //System.out.println("INIT: new blockedRect at " + xAxis + " " + yAxis);
                     }
                     if("true".equals(isHole)){
                    	 holeList.add(new Rectangle(xAxis*SIZE+8, yAxis*SIZE+8, SIZE - 16, SIZE - 16));
                     }
                 }
                 //System.out.println("blockedRects = " + blockedCount);
         }
        
        /*
        // initial spawn of charges -- needs to be put on server
        int currentCharges = 0;
        while(currentCharges < maxChargeOnLevel){
		    for(xAxis = 0; xAxis < map1.getWidth(); xAxis++){
		    	for(yAxis = 0; yAxis < map1.getHeight(); yAxis++){
                    int tileID = map1.getTileId(xAxis, yAxis, 0);
                    String value = map1.getTileProperty(tileID, "blocked", "false");
                    String isHole = map1.getTileProperty(tileID, "hole", "false");
                    
		    		if(chargeRandomizer.nextInt() % sampleSize == targetNumber &&
		    			(!value.equals("true") && !isHole.equals("true")) &&
		    			currentCharges < maxChargeOnLevel){
		    			chargeList[currentCharges] = new Rectangle(xAxis * (SIZE) + 12, yAxis * (SIZE) + 12, SIZE - 24, SIZE - 24);
		    			currentCharges += 1;
		    		}
	    			if(currentCharges >= maxChargeOnLevel){
	    				break;
	    			}		    		
		    	}
		    }
    	}*/
  
        // NEW CHARGE RANDOMIZER
        boolean hasCharge;
        int currentCharges = 0;
        while(currentCharges < maxChargeOnLevel){
        	hasCharge = false;
        	xAxis = Math.abs(chargeRandomizer.nextInt() % map1.getWidth());
        	yAxis = Math.abs(chargeRandomizer.nextInt() % map1.getHeight());
        	
            int tileID = map1.getTileId(xAxis, yAxis, 0);
            String value = map1.getTileProperty(tileID, "blocked", "false");
            String isHole = map1.getTileProperty(tileID, "hole", "false"); 
            
        	if(!value.equals("true") && 
        	   !isHole.equals("true")){
        		// check if location already has charge
        		for(int j = 0; j < chargeList.length; j++){
        			if(chargeList[j] != null){
	        			if(chargeList[j].getX() == xAxis*SIZE &&
	        			   chargeList[j].getY() == yAxis*SIZE){
	        				hasCharge = true;
	        				break;
	        			}
        			}
        		}
        		if(hasCharge == false){
        			chargeList[currentCharges] = new Rectangle(xAxis * (SIZE) + 12, yAxis * (SIZE) + 12, SIZE - 24, SIZE - 24);
        			currentCharges += 1;
        		}
        		
        	}
        }
        
        // CHARGE SOURCE LIST RANDOMIZER
        int sourceListSize = 1000;
        currentCharges = 0;
        while(currentCharges < sourceListSize){
        	hasCharge = false;
        	xAxis = Math.abs(chargeRandomizer.nextInt() % map1.getWidth());
        	yAxis = Math.abs(chargeRandomizer.nextInt() % map1.getHeight());
        	
            int tileID = map1.getTileId(xAxis, yAxis, 0);
            String value = map1.getTileProperty(tileID, "blocked", "false");
            String isHole = map1.getTileProperty(tileID, "hole", "false"); 
            
        	if(!value.equals("true") && 
        	   !isHole.equals("true")){
        		// check if location already has charge
        		chargeListSource.add(new Rectangle(xAxis * (SIZE) + 12, yAxis * (SIZE) + 12, SIZE - 24, SIZE - 24));
        		currentCharges += 1;
        	}
        }        
        
        cstation_r0 = new Rectangle(p0STATION_X,p0STATION_Y,SIZE,SIZE);
        cstation_i0 =  new Image("/data/charging-station-1-small.png");
        
        cstation_r1 = new Rectangle(p1STATION_X,p1STATION_Y,SIZE,SIZE);
        cstation_i1 = new Image("/data/charging-station-2-small.png");
        
        yourCharge = 0;
        
        plane_0 = new Entity("plane_0");
        plane_0.AddComponent( new ImageRenderComponent("Plane_0_Render", new Image("/data/sprite-1.png")) );
        plane_0.AddComponent( new TopDownMovement("Plane_0_Movement", blockedRects) );
        plane_0.setPosition(new Vector2f(64, 64));
        
        plane_1 = new Entity("plane_1");
        plane_1.AddComponent( new ImageRenderComponent("Plane_1_Render", new Image("/data/sprite-2.png")) );
        plane_1.AddComponent( new TopDownMovement("Plane_1_Movement", blockedRects) );
        plane_1.setPosition(new Vector2f(720, 520));          

        flux_r0 = new Circle(plane_0.getPosition().x+16,plane_0.getPosition().y+18,80); 
        flux_r1 = new Circle(plane_1.getPosition().x+16,plane_1.getPosition().y+18,80);
        
        
        ambience_s.loop(1.0f, 0.9f);
    }
 

    int spawnTimer = 0;
    int spawnGeneration = 0;
    
    boolean spawnCycleStart = true;
    @Override
    public void update(GameContainer gc, int delta)
			throws SlickException
    {	  	
    	spawnTimer += delta;
    	if(id == 0) 
       	{  	
        	
    		emptySlots = 0;
        	for(int k = 0; k < chargeList.length; k++){
        		if(chargeList[k] == null){
        			emptySlots += 1;
        		}
        	}
        	
        	if(emptySlots == maxChargeOnLevel){
        		spawnCharge(emptySlots);
        		//spawnTimer = 0;
        	}
    		
    		
    		double dx = Math.pow(plane_0.getPosition().x - plane_1.getPosition().x,2);
    		double dy = Math.pow(plane_0.getPosition().y - plane_1.getPosition().y,2);
    		double distance = Math.sqrt(dx+dy);
    		
    		plane_0.update(gc, null, delta);
    		
    		if(!distanceSound_s.playing()){
    			distanceSound_s.play((float)(0.7f + 150.0/distance), (float)(0.7f + 200.0/distance));
    		}    		
    		
    		if(gc.getInput().isKeyDown(gc.getInput().KEY_SPACE) && plane_0.carried_charge > 0) { 
    			f0time+=delta;
    			
    			if(f0time<500) {
	    			flux_r0.setCenterX(plane_0.getPosition().x+16);
	    			flux_r0.setCenterY(plane_0.getPosition().y+18);
	    			f0_drawer = 1;
	    			// sound effect
	    			//if(!flux_s.playing()){
	    				flux_s.play();
	    			//}	    			
    			}
    			else {
    				f0_drawer = 0;
    			}
    				
    		}	
    		else {
    			f0_drawer = 0;
    			f0time = 0;
    		}    		
    		if(f1_drawer==1) {
    			flux_r1.setCenterX(plane_1.getPosition().x);
    			flux_r1.setCenterY(plane_1.getPosition().y);
    			if(flux_r1.getRadius() >= (float) distance) {
    				if(plane_0.carried_charge > 0) {
	    				if(plane_0.hp > 1)	
	    					plane_0.hp -= plane_0.carried_charge/epsilon;
	    				else {
	    					plane_0.hp = 0;
	    					plane_0.setPosition(new Vector2f(64,64));
	    					plane_0.hp = 100000;
	    					plane_0.carried_charge = 0;
	    				}
    				}
    				
    			}
    		}
			cc = chargeCollide(plane_0.getPosition().x, plane_0.getPosition().y, "PLAYER 1");
			stationCollide(plane_0.getPosition().x, plane_0.getPosition().y,0);

			// win condition
			if(plane_0.collected_charge == LIMIT_TO_WIN){
				winner = 0;
				plane_1.collected_charge = -1;
			}
			
        	if(emptySlots > 0){
        		spawnCycleStart = false;
        	} else {
        		spawnCycleStart = true;
        	}			
			
			if(spawnCycleStart == true){
				cc = -1;
			}			
			
			ts.input = Float.toString(plane_0.getPosition().x)+";"+Float.toString(plane_0.getPosition().y) 
					+";"+Float.toString(plane_0.rotation)+";"+Integer.toString(cc)+";"+ 
					Integer.toString(plane_0.collected_charge)+";"+Integer.toString(plane_0.carried_charge)
					+";"+Integer.toString(f0_drawer)+";"+Float.toString(plane_0.hp)+
					";"+Integer.toString(lastChargeCollidedByOpponent)+";"
					+Integer.toString(winner);
			

			if(sw.getString().indexOf(';') != -1)  { 
	    	   String input [] = sw.getString().split(";");				
	    	   plane_1.getPosition().x = Float.parseFloat(input[0]);
	    	   plane_1.getPosition().y = Float.parseFloat(input[1]);
	    	   plane_1.setRotation(Float.parseFloat(input[2]));
	    	 
	    	   if(Integer.parseInt(input[3]) != -1) {
		    		chargeList[Integer.parseInt(input[3])] = null;
		    		//System.out.printf("id %s on update: removing charge %s\n", id, input[3]);
		    		//System.out.println("Oh no, player 2 got a charge!");
		    		//map1.render(0, 0);
	    	    }
		    	lastChargeCollidedByOpponent = Integer.parseInt(input[3]);
		    	//spawnCharge(lastChargeCollidedByOpponent);	    	   
	    	   
	    		plane_1.collected_charge = Integer.parseInt(input[4]);
	    		plane_1.carried_charge = Integer.parseInt(input[5]);
	    		f1_drawer = Integer.parseInt(input[6]);
	    		if(f1_drawer == 1) {
	    			flux_r1.setCenterX(plane_1.getPosition().x+16);
	    			flux_r1.setCenterY(plane_1.getPosition().y+18);
	    		}
	    		plane_1.hp = Float.parseFloat(input[7]);
	    		lastChargeCollidedByOpponent = Integer.parseInt(input[8]);
	    		winner= Integer.parseInt(input[9]);
	    		
	    	 }
	    	plane_1.update(gc, null, delta);
    	}
    	else
    	{
        	emptySlots = 0;
        	for(int k = 0; k < chargeList.length; k++){
        		if(chargeList[k] == null){
        			emptySlots += 1;
        		}
        	}
        	
        	if(emptySlots == maxChargeOnLevel){
        		spawnCharge(emptySlots);
        		//spawnTimer = 0;
        	}
    		
    		
    		double dx = Math.pow(plane_0.getPosition().x - plane_1.getPosition().x,2);
    		double dy = Math.pow(plane_0.getPosition().y - plane_1.getPosition().y,2);
    		double distance = Math.sqrt(dx+dy);
    		
    		plane_1.update(gc, null, delta);
    		
    		if(!distanceSound_s.playing()){
    			distanceSound_s.play((float)(0.7f + 150.0/distance), (float)(0.7f + 200.0/distance));
    		}  		
    		
    		if(gc.getInput().isKeyDown(gc.getInput().KEY_SPACE) && plane_1.carried_charge > 0) { 
    			f1time+=delta;


    			
    			if(f1time<500) {
	    			flux_r1.setCenterX(plane_1.getPosition().x+16);
	    			flux_r1.setCenterY(plane_1.getPosition().y+18);
	    			f1_drawer = 1;
	    			// sound effect
	    			//if(!flux_s.playing()){
	    				flux_s.play();
	    			//}	    			
    			}
    			else {
    				f1_drawer = 0;
    			}
    				
    		}	
    		else {
    			f1_drawer = 0;
    			f1time = 0;
    		}
    		if(f0_drawer==1) {
    			flux_r0.setCenterX(plane_0.getPosition().x);
    			flux_r0.setCenterY(plane_0.getPosition().y);
    			if(flux_r0.getRadius() >= (float) distance) {
    				if(plane_1.carried_charge > 0) {
	    				if(plane_1.hp > 1)
	    					plane_1.hp -= plane_1.carried_charge/epsilon;
	    				else {
	    					plane_1.hp = 0;
	    					plane_1.setPosition(new Vector2f(720,520));
	    					plane_1.hp = 100000;
	    					plane_1.carried_charge = 0;
	    				}
    				}	
    			}
    		}
			cc = chargeCollide(plane_1.getPosition().x, plane_1.getPosition().y, "PLAYER 2");
			stationCollide(plane_1.getPosition().x, plane_1.getPosition().y,1);

			if(plane_1.collected_charge == LIMIT_TO_WIN){
				winner = 1;
				plane_0.collected_charge = -1;
			}			
			
        	if(emptySlots > 0 && emptySlots < 10){
        		spawnCycleStart = false;
        	} else {
        		spawnCycleStart = true;
        	}	
			
			
			if(spawnCycleStart == true){
				cc = -1;
			}	
			
			ts.input = Float.toString(plane_1.getPosition().x)+";"+Float.toString(plane_1.getPosition().y) 
					+";"+Float.toString(plane_1.rotation)+";"+Integer.toString(cc)+";"+
					Integer.toString(plane_1.collected_charge)+";"+Integer.toString(plane_1.carried_charge)
					+";"+Integer.toString(f1_drawer)+";"+Float.toString(plane_1.hp)+
					";"+Integer.toString(lastChargeCollidedByOpponent)+";"+
					Integer.toString(winner);

	    	if(sw.getString().indexOf(';') != -1)  { 
	    		String input [] = sw.getString().split(";");
		    	plane_0.getPosition().x = Float.parseFloat(input[0]);
		    	plane_0.getPosition().y = Float.parseFloat(input[1]);
		    	plane_0.setRotation(Float.parseFloat(input[2]));
		    	
		    	if(Integer.parseInt(input[3]) != -1) {
	    			chargeList[Integer.parseInt(input[3])] = null;
		    		//System.out.printf("id %s on update: removing charge %s\n", id, input[3]);	    			
	    			//System.out.println("oh no, Player 1 got a charge!");
	    			//map1.render(0, 0);	
		    	}
		    	lastChargeCollidedByOpponent = Integer.parseInt(input[3]);
		    	//spawnCharge(lastChargeCollidedByOpponent);
		    		plane_0.collected_charge = Integer.parseInt(input[4]);
		    		plane_0.carried_charge = Integer.parseInt(input[5]);
		    		f0_drawer = Integer.parseInt(input[6]);
		    		if(f0_drawer == 1) {
		    			flux_r0.setCenterX(plane_0.getPosition().x+16);
		    			flux_r0.setCenterY(plane_0.getPosition().y+18);
		    		}
		    		plane_0.hp = Float.parseFloat(input[7]);
		    		//input[8] not used
		    		winner = Integer.parseInt(input[9]);
		    }	
	    	plane_0.update(gc, null, delta);
    	}
    }
 
    public void render(GameContainer gc, Graphics g)
			throws SlickException
    {   	
    	
    	for(int i = 0; i < holeList.size(); i++){
    		g.draw(holeList.get(i));
    	}    	
    	
    	map1.render(0,0);    	
    	
    	/*if(Integer.parseInt(sw.getString().split(";")[3]) != -1) {
			chargeList.remove(Integer.parseInt(sw.getString().split(";")[3]));
		}*/
    	
    	//g.draw(cstation_r0);
    	g.drawImage(cstation_i0, p0STATION_X, p0STATION_Y);
    	g.drawString(Integer.toString(plane_0.collected_charge),p0STATION_X+35,p0STATION_Y+10);
    	
    	//g.draw(cstation_r1);
    	g.drawImage(cstation_i1,p1STATION_X,p1STATION_Y);
    	g.drawString(Integer.toString(plane_1.collected_charge),p1STATION_X-12,p1STATION_Y+5);
    	
      	if(f0_drawer==1) {
      		g.draw(flux_r0);
      	}	
      	if(f1_drawer==1) {
      		g.draw(flux_r1);
      	}
      	
    	for(int i = 0; i < blockedRects.size(); i++){
    		g.draw(blockedRects.get(i));
    	}
    	for(int i = 0; i < chargeList.length; i++){
    		if(chargeList[i] != null){
    			g.draw(chargeList[i]);
    		}
    	}
   
    	//if(id == 0){
    		g.drawString(Integer.toString(/*yourCharge*/plane_0.carried_charge), 
    					plane_0.getPosition().x, plane_0.getPosition().y - 16);
    		g.drawString(Float.toString(/*yourCharge*/plane_0.hp), 
					plane_0.getPosition().x, plane_0.getPosition().y + 28);
    	//} else {
    		g.drawString(Integer.toString(/*yourCharge*/plane_1.carried_charge), 
    					plane_1.getPosition().x, plane_1.getPosition().y - 16); 
    		g.drawString(Float.toString(/*yourCharge*/plane_1.hp), 
					plane_1.getPosition().x, plane_1.getPosition().y + 28);
    	//}
 
    	plane_0.render(gc, null, g);
    	plane_1.render(gc, null, g);   		
    		
    	g.setDrawMode(Graphics.MODE_ALPHA_MAP);
    	black.draw(0,0);
    	
    	if(id == 0){
    		light.drawCentered(plane_0.getPosition().x + 16, plane_0.getPosition().y + 16);

    	} else {
    		light.drawCentered(plane_1.getPosition().x + 16, plane_1.getPosition().y + 16);
    		
    	}
    	
        g.setDrawMode(Graphics.MODE_ALPHA_BLEND); 
    	black.draw(0,0);
    	
    	g.setDrawMode(Graphics.MODE_NORMAL);
    	if(id == 0){
        	g.drawImage(cstation_i0, p0STATION_X, p0STATION_Y);
        	g.drawString(Integer.toString(plane_0.collected_charge),p0STATION_X+35,p0STATION_Y+10);
        	g.drawString("Enemy charge level: " + plane_1.collected_charge+" / "+ maxChargeOnLevel, 550, 580);
    	} else {
        	g.drawImage(cstation_i1,p1STATION_X,p1STATION_Y);
        	g.drawString(Integer.toString(plane_1.collected_charge),p1STATION_X-12,p1STATION_Y+5);
        	g.drawString("Enemy charge level: " + plane_0.collected_charge + " / "+ maxChargeOnLevel, 550, 580);         	
    	}
    	
    	if(flashScreen == true){
    		g.fillRect(0, 0, 800, 600);
    		flashScreen = false;
    	}
    	
    	if(winner == -1){
    		//black.draw(0,0);
    		//g.drawString("No winner yet", 400, 300);
    	} else if(winner == 0){
    		black.draw(0,0);
    		g.drawString("Blue wins!", 350, 300);
    	} else {
    		black.draw(0,0);
    		g.drawString("Green wins!", 350, 300);
    	}
    	
    	
    	g.drawString("Time:" + spawnTimer/1000, 720, 8);
    	g.drawString("Charges left on map: " + (maxChargeOnLevel - emptySlots), 10, 580);    	
    }
    
    
    private int chargeCollide(float x, float y, String pl){
    	Rectangle self = new Rectangle(x, y, SIZE, SIZE);
        for(int i = 0; i < chargeList.length; i++){
        	if(chargeList[i] != null){
		        if(self.intersects(chargeList[i])){
		           System.out.printf("CHARGE: Collision detected at (%s, %s) by %s\n", x, y, pl);
		           chargeList[i] = null;
		           yourCharge += 1;
		           if(id == 0 && pl.equals("PLAYER 1")) {
		        	   plane_0.carried_charge = yourCharge;
		           }
		           else {
		        	   plane_1.carried_charge = yourCharge;   
		           }
		           float pitchAndVolumeFactor = ((float)yourCharge)/12.0f;
		           pickupCharge.play(1.0f + pitchAndVolumeFactor, 1);
		           //lastChargeCollidedByOpponent = lastChargeCollidedWith;
		           lastChargeCollidedWith = i;
		           System.out.printf("id %s from chargeCollide | emptySlots = %s removing at slot %s\n", id, emptySlots, i);
		           return i;
		        }
        	}
         }
         return lastChargeCollidedWith;
    }  
    
    private /*int*/void stationCollide(float x, float y,int pl) {
    	int totalChargeCollected = 0;
    	Rectangle self = new Rectangle(x, y, SIZE, SIZE);
    	if(pl == 0){
    		if(self.intersects(cstation_r0)){
    			plane_0.collected_charge += yourCharge;    
    			
    			if(yourCharge > 0){
    				chargeStation_s.play(1 + ((float)plane_0.collected_charge)/50.0f, 1);
    			}
    			
    			yourCharge = 0;
    			plane_0.carried_charge = 0;
    			//totalChargeCollected = plane_0.collected_charge;
    		}
    	}
    	else {
    		if(self.intersects(cstation_r1)){
    			plane_1.collected_charge += yourCharge; 
    			
    			if(yourCharge > 0){
        			chargeStation_s.play(1 + ((float)plane_1.collected_charge)/50.0f, 1);
    			}    			
    			
    			yourCharge = 0;
    			plane_1.carried_charge = 0;
    			//totalChargeCollected = plane_1.collected_charge;
    		}
    	}
    	/*return totalChargeCollected;*/
    }
    
    private void spawnCharge(int emptySlotsArg){
		System.out.println("spawnCharge called; arg = " + emptySlotsArg + ", id = " + id);
		
		for(int i = 0; i < chargeList.length; i++){
			if( chargeList[i] == null ){
				chargeList[i] = chargeListSource.remove(sourceListSize - 1);
				sourceListSize -= 1;
        		pickupRespawn_s.play(1.0f, 0.7f);
        		flashScreen = true;
        		System.out.printf("  spawnCharge %s: %s,%s | id = %s\n", i, chargeList[i].getX(), chargeList[i].getY(), id);
			}
		}
		System.out.println("SourceListSize = " + sourceListSize);
}   
    
    
   /*
    private void spawnCharge(int emptySlotsArg){
    	if(emptySlotsArg > 1){
    		System.out.println("spawnCharge called; arg = " + emptySlotsArg + ", id = " + id);
    	// randomly put a new charge again
    		
    	//for(int k = 0; k < chargeList.length)
			int xAxis = 0;
			int yAxis = 0;
			
			int emptySlot = -1;
			// choose empty location       
	        // place on unoccupied, un-constantly deleting slot
			
			int counter = 0;
	        while(true){
	        	xAxis = Math.abs(chargeRandomizer.nextInt() % map1.getWidth());
	        	yAxis = Math.abs(chargeRandomizer.nextInt() % map1.getHeight());
	        	
	        	System.out.println("spawnCharge: location search iteration " + counter + 
	        			": (xAxis, yAxis) = (" + xAxis + ", " + yAxis +") ");
	        	
	            int tileID = map1.getTileId(xAxis, yAxis, 0);
	            String value = map1.getTileProperty(tileID, "blocked", "false");
	            String isHole = map1.getTileProperty(tileID, "hole", "false"); 
	            
	        	if(!value.equals("true") && 
	        	   !isHole.equals("true")){
	        		System.out.println("Found empty location! " + xAxis + "," + yAxis + " ");
	        		break;
	        	}
	        } 
	        
			for(int j = 0; j < chargeList.length; j++){
				if( chargeList[j] == null &&
					j != lastChargeCollidedByOpponent &&
					j != lastChargeCollidedWith){
						emptySlot = j;
						System.out.println("Found empty slot! Slot " + j);
						break;
				}
			}
			
			if(emptySlot != -1){		        
		        chargeList[emptySlot] = new Rectangle(xAxis * (SIZE) + 12, yAxis * (SIZE) + 12, SIZE - 24, SIZE - 24);
	    		System.out.println("NEW CHARGE AT " + xAxis + "," + yAxis + " from id = " + id);
			}
    	}
}
        */
 
    public static void main(String[] args)
			throws SlickException
    {
    	String ip = JOptionPane.showInputDialog("Enter IP");
    	//String ip = "127.0.0.1";
    	//String port = JOptionPane.showInputDialog("Enter port (8888 optimal)");
         AppGameContainer app =
			new AppGameContainer( new Intermission1SlickBasicGame(ip) );
 
         app.setDisplayMode(800, 600, false);
         app.setShowFPS(false);
         app.setIcon("/data/charging-station-1-small.png");
         app.start();
    }
}
