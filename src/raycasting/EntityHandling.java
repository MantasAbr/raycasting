package raycasting;

import sprites.Direction;
import sprites.Entity;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

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

    private Direction findShortestPath(boolean[][] lab, int hx, int hy, int px, int py){
        Queue<PathfindingNode> queue = new ArrayDeque<>();

        boolean[][] discovered = new boolean[10][10];

        discovered[hx][hy] = true;
        queue.add(new PathfindingNode(hx, hy, null));

        while (!queue.isEmpty()) {
            PathfindingNode node = queue.poll();

            // Go breath-first into each direction
            for (Direction dir : Direction.values()) {
                int newX = node.x + dir.getDx();
                int newY = node.y + dir.getDy();
                Direction newDir = node.initialDir == null ? dir : node.initialDir;

                // Mouse found?
                if (newX == px && newY == py) {
                    return newDir;
                }

                // Is there a path in the direction (= is it a free field in the labyrinth)?
                // And has that field not yet been discovered?
                if (!lab[newY][newX] && !discovered[newY][newX]) {
                    // "Discover" and enqueue that field
                    discovered[newY][newX] = true;
                    queue.add(new PathfindingNode(newX, newY, newDir));
                }
            }
        }

        throw new IllegalStateException("No path found");
    }

    private static class PathfindingNode {
        final int x;
        final int y;
        final Direction initialDir;

        public PathfindingNode(int x, int y, Direction initialDir) {
            this.x = x;
            this.y = y;
            this.initialDir = initialDir;
        }
    }
}
