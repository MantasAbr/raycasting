package raycasting;
import levels.Level;
import levels.LevelDoorMesh;

import java.util.ArrayList;

/**
 *
 * @author Mantas Abramavičius
 */
public class ActionHandling {
    
    public Camera camera;
    public Screen screen;
    public Raycasting raycasting;
    public int forwardBlockX;
    public int forwardBlockY;

    public boolean canOpen;
    public boolean canEnterNewLevel;

    private int oldX, newX = 0;
    private int oldY, newY = 0;
    public static double rotationValue = 0;
    public static boolean turningLeft, turningRight = false;
    
    public ActionHandling(Camera camera, Screen screen, Raycasting raycasting){
        this.camera = camera;
        this.screen = screen;
        this.raycasting = raycasting;
        
        forwardBlockX = 0;
        forwardBlockY = 0;
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
        canEnterNewLevel = false;
        
        if(canOpenDoor()){
            canOpen = true;
        }

        if(canOpenNewLevelDoor()){
            canEnterNewLevel = true;
        }
    }
    
    public void ApplyBlockChanges(int[][] gameMap){
        
        if(canOpen && camera.action){
            gameMap[forwardBlockX][forwardBlockY] = 0;
        }
    }

    public void ChangeLevel(ArrayList<Level> levels, ArrayList<LevelDoorMesh> doorMeshes){
        if(canEnterNewLevel && camera.action){

            Raycasting.CURRENT_LEVEL = screen.lookingAtMeshId;


            //Sets the player's (cameras) location
            camera.setXPos(levels.get(Raycasting.CURRENT_LEVEL).getPlayerLocX());
            camera.setYPos(levels.get(Raycasting.CURRENT_LEVEL).getPlayerLocY());


            //Set the level
            screen.setMap(levels.get(Raycasting.CURRENT_LEVEL).getMap());
            //Set the door mesh for the new level
            screen.setDoorMap(doorMeshes.get(Raycasting.CURRENT_LEVEL).getMap());
        }

    }

    public void mouseMovementHandling(double sensitivity){
        newX = camera.mouseX;
        newY = camera.mouseY;

        rotationValue = (Math.abs(newX - oldX) / sensitivity);

        if(newX > oldX)
            turningRight = true;
        if(newX < oldX)
            turningLeft = true;
        if(newX == oldX){
            turningRight = false;
            turningLeft = false;
        }

        if(newX <= 10 || newY <= 10)
            raycasting.robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);
        if(newX >= Raycasting.WINDOW_WIDTH - 10 || newY >= Raycasting.WINDOW_HEIGHT - 10)
            raycasting.robot.mouseMove(Raycasting.SCREEN_WIDTH / 2, Raycasting.SCREEN_HEIGHT / 2);

        oldX = newX;
        oldY = newY;
    }
    
    private boolean canOpenDoor(){
        return screen.lookingAtTextureId == 5 && screen.distanceToWall <= 1;
    }

    private boolean canOpenNewLevelDoor(){return screen.lookingAtTextureId == 6 && screen.distanceToWall <= 1;}
}
