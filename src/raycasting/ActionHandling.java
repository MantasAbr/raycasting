package raycasting;
import input.Input;
import levels.Level;
import levels.LevelDoorMesh;
import sounds.Sounds;

import java.util.ArrayList;

/**
 *
 * @author Mantas Abramavičius
 */
public class ActionHandling {
    
    public Camera camera;
    private Camera tempCamera;
    public Screen screen;
    public Input input;
    public Raycasting raycasting;
    private ArrayList<Sounds> sounds;
    public int forwardBlockX;
    public int forwardBlockY;

    public int runningSoundFrames;

    public boolean canOpen;
    public boolean canEnterNewLevel;
    public boolean levelChange;
    private boolean playerDirectionIsChanged = false;
    
    public ActionHandling(Camera camera, Screen screen, ArrayList<Sounds> sounds, Input input, Raycasting raycasting){
        this.camera = camera;
        this.tempCamera = camera;
        this.screen = screen;
        this.sounds = sounds;
        this.input = input;
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
        
        if(canOpen && input.action.isPressed()){
            gameMap[forwardBlockX][forwardBlockY] = 0;
        }
    }

    public void HandleButtonCombos(){

        input.isSprinting = false;
        input.isWalking = false;

        if(playerIsSprinting())
            input.isSprinting = true;
        if(playerIsWalking())
            input.isWalking = true;

    }

    public void ChangeLevel(ArrayList<Level> levels, ArrayList<LevelDoorMesh> doorMeshes){

        levelChange = false;

        if(canEnterNewLevel && input.action.isPressed()){

            sounds.get(2).PlaySound(false);
            runningSoundFrames = sounds.get(2).clip.getFrameLength();
            levelChange = true;

            //Get the latest player position before level change
            levels.get(Raycasting.CURRENT_LEVEL).setPlayerLocX(camera.xPos);
            levels.get(Raycasting.CURRENT_LEVEL).setPlayerLocY(camera.yPos);

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

    public void resetPlayerFacingPosition(){
        if(!playerDirectionIsChanged){
            camera.xDir = tempCamera.xDir;
            camera.yDir = tempCamera.yDir;
            playerDirectionIsChanged = true;
        }
    }

    public void setPlayerFacingPosition(){
        tempCamera.xDir = camera.xDir;
        tempCamera.yDir = camera.yDir;
        playerDirectionIsChanged = false;
    }
    
    private boolean canOpenDoor(){
        return screen.lookingAtTextureId == 5 && screen.distanceToWall <= 1;
    }

    private boolean canOpenNewLevelDoor(){return screen.lookingAtTextureId == 6 && screen.distanceToWall <= 1;}

    private boolean playerIsSprinting(){
        return input.forward.isPressed() && input.shift.isPressed();
    }

    private boolean playerIsWalking(){
        return input.forward.isPressed() || input.back.isPressed();
    }
}
