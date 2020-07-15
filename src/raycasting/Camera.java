package raycasting;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 *
 * @author Mantas Abramavičius
 */
public class Camera implements KeyListener{

    /*  
        xPos and yPos are the location of the player on the 2D map.
        xDir and yDir are the x and y components of a vector that points in the
        direction that the player is facing.
        xPlane and yPlane is always perpendicular to the direction vector, 
        and it points to the farthest edge of the camera's field of view on
        one side.
    */
    
    public double xPos, yPos, xDir, yDir, xPlane, yPlane;
    public boolean left, right, forward, back, action, shift, debug;
    public final double MOVE_SPEED = .08;
    public final double FASTER_MOVE_SPEED = .12;
    public final double ROTATION_SPEED = .045;
    
    public Camera(double x, double y, double xd, double yd, double xp, double yp){
        xPos = x;
        yPos = y;
        xDir = xd;
        yDir = yd;
        xPlane = xp;
        yPlane = yp;
    }
    
    @Override
    public void keyTyped(KeyEvent key) {
        
    }

    @Override
    public void keyPressed(KeyEvent key) {
        if((key.getKeyCode() == KeyEvent.VK_LEFT) || (key.getKeyCode() == KeyEvent.VK_A)){
            left = true;
            //System.out.println("Left pressed");
        }		
	if((key.getKeyCode() == KeyEvent.VK_RIGHT) || (key.getKeyCode() == KeyEvent.VK_D)){
            right = true;
            //System.out.println("Right pressed");
        }
		
	if((key.getKeyCode() == KeyEvent.VK_UP) || (key.getKeyCode() == KeyEvent.VK_W)){
            forward = true;
            //System.out.println("Up pressed");
        }
		
	if((key.getKeyCode() == KeyEvent.VK_DOWN) || (key.getKeyCode() == KeyEvent.VK_S)){
            back = true;
            //System.out.println("Down pressed");
        }
	
        if((key.getKeyCode() == KeyEvent.VK_E)){
            action = true;
            
        }
        
        if((key.getKeyCode() == KeyEvent.VK_SHIFT)){
            shift = true;
            
        }
        
        if((key.getKeyCode() == KeyEvent.VK_F3)){
            if(debug)
                debug = false;
            else
                debug = true;        
        }
    }

    @Override
    public void keyReleased(KeyEvent key) {
        if((key.getKeyCode() == KeyEvent.VK_LEFT) || (key.getKeyCode() == KeyEvent.VK_A)){
            left = false;
            //System.out.println("Left released");
        }		
	if((key.getKeyCode() == KeyEvent.VK_RIGHT) || (key.getKeyCode() == KeyEvent.VK_D)){
            right = false;
            //System.out.println("Right released");
        }
		
	if((key.getKeyCode() == KeyEvent.VK_UP) || (key.getKeyCode() == KeyEvent.VK_W)){
            forward = false;
            //System.out.println("Up released");
        }
		
	if((key.getKeyCode() == KeyEvent.VK_DOWN) || (key.getKeyCode() == KeyEvent.VK_S)){
            back = false;
            //System.out.println("Down released");
        }
        
        if((key.getKeyCode() == KeyEvent.VK_E)){
            action = false;
            
        }
        
        if((key.getKeyCode() == KeyEvent.VK_SHIFT)){
            shift = false;
            
        }
    }
    
    public void update(int[][] map) {
        if(forward) {
            if(shift){
                if(map[(int)(xPos + xDir * MOVE_SPEED)][(int)yPos] == 0) {
                    xPos+=xDir*FASTER_MOVE_SPEED;
                }
                if(map[(int)xPos][(int)(yPos + yDir * MOVE_SPEED)] ==0)
                    yPos+=yDir*FASTER_MOVE_SPEED;
            }
            else{
                if(map[(int)(xPos + xDir * MOVE_SPEED)][(int)yPos] == 0) {
                    xPos+=xDir*MOVE_SPEED;
                }
                if(map[(int)xPos][(int)(yPos + yDir * MOVE_SPEED)] ==0)
                    yPos+=yDir*MOVE_SPEED;
            }
        }
        if(back) {
            if(map[(int)(xPos - xDir * MOVE_SPEED)][(int)yPos] == 0)
                    xPos-=xDir*MOVE_SPEED;
            if(map[(int)xPos][(int)(yPos - yDir * MOVE_SPEED)]==0)
                    yPos-=yDir*MOVE_SPEED;
        }
        if(right) {
                double oldxDir=xDir;
                xDir=xDir*Math.cos(-ROTATION_SPEED) - yDir*Math.sin(-ROTATION_SPEED);
                yDir=oldxDir*Math.sin(-ROTATION_SPEED) + yDir*Math.cos(-ROTATION_SPEED);
                double oldxPlane = xPlane;
                xPlane=xPlane*Math.cos(-ROTATION_SPEED) - yPlane*Math.sin(-ROTATION_SPEED);
                yPlane=oldxPlane*Math.sin(-ROTATION_SPEED) + yPlane*Math.cos(-ROTATION_SPEED);
        }
        if(left) {
                double oldxDir=xDir;
                xDir=xDir*Math.cos(ROTATION_SPEED) - yDir*Math.sin(ROTATION_SPEED);
                yDir=oldxDir*Math.sin(ROTATION_SPEED) + yDir*Math.cos(ROTATION_SPEED);
                double oldxPlane = xPlane;
                xPlane=xPlane*Math.cos(ROTATION_SPEED) - yPlane*Math.sin(ROTATION_SPEED);
                yPlane=oldxPlane*Math.sin(ROTATION_SPEED) + yPlane*Math.cos(ROTATION_SPEED);
        }
    }
}
