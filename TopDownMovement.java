import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.geom.Rectangle;

import java.util.ArrayList;


public class TopDownMovement extends Component {
 
	float direction;
	ArrayList<Rectangle> blockedRects;
    private static final int SIZE = 32; 
    
	float speed;
 
	public TopDownMovement( String id, ArrayList<Rectangle> br )
	{
		this.blockedRects = br;
		this.id = id;
	}
 
	public float getSpeed()
	{
		return speed;
	}
 
	public float getDirection()
	{
		return direction;
	}
 
	@Override
	public void update(GameContainer gc, StateBasedGame sb, int delta) {
		float speed = 0.2f;
		float rotation = owner.getRotation();
		float scale = owner.getScale();
		Vector2f position = owner.getPosition();
 
		Input input = gc.getInput();
 
        if(input.isKeyDown(Input.KEY_A))
        {
        	//rotation += -0.2f * delta;
        	if(!isBlocked(position.x - delta * speed, position.y)){
        		position.x += -speed * delta;
        	}
        	rotation = 270;
        }
 
        if(input.isKeyDown(Input.KEY_D))
        {
        	//rotation += 0.2f * delta;
        	if(!isBlocked(position.x + delta * speed, position.y)){
        		position.x += speed * delta;
        	}
        	rotation = 90;

        }
 
        if(input.isKeyDown(Input.KEY_W))
        {
            float hip = speed * delta;
            if(!isBlocked(position.x, position.y - delta * speed)){
            	position.y -= hip;
            }
            rotation = 0;
        }
        
        if(input.isKeyDown(Input.KEY_S))
        {
        	float hip = speed * delta;
        	if(!isBlocked(position.x, position.y + delta * speed)){
        		position.y += hip;
        	}
            rotation = 180;
        }

        
        //System.out.println(owner.id + "r=" + Float.toString(rotation));
        
		owner.setPosition(position);
		owner.setRotation(rotation);
		owner.setScale(scale);
	}
	
    private boolean isBlocked(float x, float y){
    	Rectangle self = new Rectangle(x + 3, y + 3, SIZE - 6, SIZE - 6);
        for(int i = 0; i < blockedRects.size(); i++){
            if(self.intersects(blockedRects.get(i))){
               //System.out.printf("Collision detected at (%s, %s).\n", x, y);
               return true;
            }
         }
         //System.out.printf("No collision has been detected.\n");
         return false;
    }    	
 
}