package raycasting;

/**
 *
 * @author Mantas Abramavičius
 */
public class ActionHandling {
    
    public Camera camera;
    public Screen screen;
    public int forwardBlockX;
    public int forwardBlockY;
    public boolean canOpen;
    
    public ActionHandling(Camera camera, Screen screen){
        this.camera = camera;
        this.screen = screen;
        
        forwardBlockX = 0;
        forwardBlockY = 0;
        canOpen = false;
    }
    
    /**
     * Find the coordinates of the block that the player is facing
     */
    public void GetNextBlock(){
        //East direction
        if(screen.rayX >= -.5 && screen.rayX <= .5 && screen.rayY >= 1){
            forwardBlockX = (int)camera.xPos;
            forwardBlockY = (int)camera.yPos + 1;
        }
        //North direction
        if(screen.rayX >= 1 && screen.rayY >= -.5 && screen.rayY <= .5){
            forwardBlockX = (int)camera.xPos + 1;
            forwardBlockY = (int)camera.yPos;
        }
        //West direction
        if(screen.rayX >= -.5 && screen.rayX <= .5 && screen.rayY <= -1){
            forwardBlockX = (int)camera.xPos;
            forwardBlockY = (int)camera.yPos - 1;
        }
        //South direction
        if(screen.rayX <= -1 && screen.rayY >= -.5 && screen.rayY <= .5){
            forwardBlockX = (int)camera.xPos - 1;
            forwardBlockY = (int)camera.yPos;
        }
    }
    
    public void CheckForActions(){
        canOpen = false;
        
        if(canOpenDoor()){
            canOpen = true;
        }
    }
    
    public void ApplyBlockChanges(int[][] gameMap){
        
        if(canOpen && camera.action){
            gameMap[forwardBlockX][forwardBlockY] = 0;
        }
    }
    
    private boolean canOpenDoor(){
        return screen.lookingAtTextureId == 5 && screen.distanceToWall <= 1;
    }
}
