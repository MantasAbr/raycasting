package raycasting;

import sprites.Entity;

import java.util.ArrayList;

public class EntityHandling {

    private Camera camera;
    private ArrayList<Entity> entities;
    private Player player;

    private final double distanceToDealDamage = 0.5;

    public EntityHandling(Camera camera, ArrayList<Entity> entities, Player player){
        this.camera = camera;
        this.entities = entities;
        this.player = player;
    }

    public void moveEntities(){

        for (Entity entity : entities) {
            if(entity.isHostile()){
                if (entity.getXLoc() > camera.xPos)
                    entity.setXLoc(entity.getXLoc() - 0.01);
                if (entity.getXLoc() < camera.xPos)
                    entity.setXLoc(entity.getXLoc() + 0.01);
                if (entity.getYLoc() > camera.yPos)
                    entity.setYLoc(entity.getYLoc() - 0.01);
                if (entity.getYLoc() < camera.yPos)
                    entity.setYLoc(entity.getYLoc() + 0.01);
            }
        }
    }

    public void applyHostileEntityDamage(){
        for(Entity entity : entities){
            if(entity.getDistance() <= distanceToDealDamage && entity.isHostile())
                player.setHealthValue(player.getHealthValue() - 0.1);
        }
    }
}
