package raycasting;

/**
 *
 * @author Mantas Abramavičius
 */
public class ActionHandling {
    
    public Camera camera;
    public Screen screen;
    public int[][] map;
    public int forwardBlockX;
    public int forwardBlockY;
    public boolean canOpen;
    
    public ActionHandling(Camera camera, Screen screen, int[][] map){
        this.camera = camera;
        this.screen = screen;
        this.map = map;
        
        forwardBlockX = 0;
        forwardBlockY = 0;
        canOpen = false;
    }
    
    /**
     * Find the coordinates of the block that the player is facing
     */
    public void GetNextBlock(){
        
    }
    
    public void CheckForActions(){
        canOpen = false;
        
        if(screen.lookingAtTextureId == 7 && screen.distanceToWall <= 1){
            canOpen = true;
        }
    }
    
    public void ApplyBlockChanges(){
        
        if(canOpen && camera.action){
            
        }
    }
}
