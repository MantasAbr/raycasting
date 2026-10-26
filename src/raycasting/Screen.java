package raycasting;
import sprites.Entity;
import levels.Level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author Mantas Abramavičius
 */
public class Screen {
    
    public int[][] map;
    public int[][] doorMap;
    public int mapWidth, mapHeight, width, height;
    public ArrayList<Texture> textures;
    public ArrayList<ArrayList<Entity>> allLevelSprites;
    public double distanceToWall;
    public int lookingAtTextureId;
    public int lookingAtMeshId;
    public int lookingAtTileX, lookingAtTileY;
    public boolean lookingAtThinWall;
    public double rayX, rayY;

    public double pitch = 0;
    public double posZ = 0;

    private final int textureSize = 64;

    private int ceilingTexture = 2;
    private int floorTexture = 0;

    private final int[] wallDrawStarts;
    private final int[] wallDrawEnds;

    private int numberOfSprites;
    private double ZBuffer[];
    private int spriteOrder[];
    private double spriteDistance[];
    private final Fog fog;
    private final double[] fogHorizontalRayLengthsSquared;
    private WorldGeometry geometry;
    private final WorldGeometry.Hit wallHit = new WorldGeometry.Hit();
    
    public Screen(int[][] map, int[][] doorMap, int mapWidth, int mapHeight, ArrayList<Texture> textures,
                  ArrayList<ArrayList<Entity>> allLevelSprites, int currentLevel, int width, int height){
        this.map = map;
        geometry = new WorldGeometry(map);
        this.doorMap = doorMap;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.textures = textures;
        this.allLevelSprites = allLevelSprites;
        this.width = width;
        this.height = height;
        fog = new Fog(Raycasting.FOG_DENSITY, Raycasting.FOG_COLOR);
        fogHorizontalRayLengthsSquared = new double[width];

        wallDrawStarts = new int[width];
        wallDrawEnds = new int[width];

        ZBuffer = new double[width];
        numberOfSprites = allLevelSprites.get(currentLevel).size();
        spriteOrder = new int[numberOfSprites];
        spriteDistance = new double[numberOfSprites];
    }

    public void setMap(int[][] newMap){
        this.map = newMap;
        geometry = new WorldGeometry(newMap);
        mapHeight = newMap.length;
        mapWidth = newMap[0].length;
    }

    public void setLevel(Level level) {
        map = level.getMap();
        mapHeight = level.getMapHeight();
        mapWidth = level.getMapWidth();
        geometry = new WorldGeometry(map, level.getWalls());
    }

    public WorldGeometry getGeometry() {
        return geometry;
    }

    public void setDoorMap(int[][] newMap){
        this.doorMap = newMap;
    }

    public void setNumberOfSprites(int currentLevel){
        numberOfSprites = allLevelSprites.get(currentLevel).size();
    }


    /** Draw opaque walls first, then only the visible floor/ceiling, then sprites. */
    public int[] updateWorld(Camera camera, int[] pixels, int currentLevel) {
        updateWalls(camera, pixels);
        updateFloorAndCeiling(camera, pixels, true);
        return updateSprites(camera, pixels, currentLevel);
    }

    public int[] updateWalls(Camera camera, int[] pixels){
        for(int x = 0; x < width; x++){            
            // Cast one ray per screen column; geometry tests partitions inside each visited cell.
            
            double cameraX = 2 * x / (double)(width) - 1;
            double rayDirX = camera.xDir + camera.xPlane * cameraX;
            double rayDirY = camera.yDir + camera.yPlane * cameraX;

            boolean hit = geometry.raycast(camera.xPos, camera.yPos, rayDirX, rayDirY, wallHit);
            if (x == width / 2) {
                lookingAtTextureId = hit ? wallHit.textureId : 0;
                lookingAtMeshId = hit && !wallHit.thinWall ? doorMap[wallHit.tileX][wallHit.tileY] : -1;
                lookingAtTileX = hit ? wallHit.tileX : -1;
                lookingAtTileY = hit ? wallHit.tileY : -1;
                lookingAtThinWall = hit && wallHit.thinWall;
                distanceToWall = wallHit.distance;
                rayX = rayDirX;
                rayY = rayDirY;
            }
            if (!hit) {
                wallDrawStarts[x] = wallDrawEnds[x] = 0;
                ZBuffer[x] = Double.POSITIVE_INFINITY;
                continue;
            }
            int side = wallHit.side;
            double perpWallDist = Math.max(1e-9, wallHit.distance);

            //Now calculate the height of the wall based on the distance from the camera
            int lineHeight;
            if(perpWallDist > 0)
                lineHeight = Math.abs((int)(height / perpWallDist));
            else
                lineHeight = height;

            //calculate lowest and highest pixel to fill in current stripe
            int drawStart = (int)(-lineHeight / 2 + height / 2 + pitch + (posZ / perpWallDist));
            if(drawStart < 0)
                drawStart = 0;
            int drawEnd = (int)(lineHeight / 2 + height / 2 + pitch + (posZ / perpWallDist));
            if(drawEnd >= height)
                drawEnd = height - 1;
            wallDrawStarts[x] = drawStart;
            wallDrawEnds[x] = drawEnd;


            //add a texture
            int texNum = wallHit.textureId - 1;
            double wallX = side == 1 ? wallHit.x : wallHit.y;
            wallX -= Math.floor(wallX);
            //x coordinate on the texture
            int texX = (int)(wallX * (textureSize));
            if(side == 0 && rayDirX > 0) texX = textureSize - texX - 1;
            if(side == 1 && rayDirY < 0) texX = textureSize - texX - 1;

            double step = 1.0 * textureSize / lineHeight;
            double texPos = (drawStart - pitch - (posZ / perpWallDist) - height / 2 + lineHeight / 2) * step;


            //calculate y coordinate on texture
            double horizontalRayLengthSquared = rayDirX * rayDirX + rayDirY * rayDirY;

            for(int y = drawStart; y < drawEnd; y++) {
                //int texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
                int texY = (int)texPos & (textureSize - 1);
                texPos += step;
                int color;
                if(side == 0)
                    color = textures.get(texNum).pixels[texX + (texY * textureSize)];
                else
                    color = (textures.get(texNum).pixels[texX + (texY * textureSize)]>>1) & 8355711;//Make y sides darker

                color = applyFogAtPixel(color, perpWallDist, horizontalRayLengthSquared, y);
                pixels[x + y*(width)] = color;
            }

            ZBuffer[x] = perpWallDist;

        }
        return pixels;
    }

    public int[] updateFloorAndCeiling(Camera camera, int[] pixels){
        return updateFloorAndCeiling(camera, pixels, false);
    }

    private int[] updateFloorAndCeiling(Camera camera, int[] pixels, boolean skipWalls){
        //FLOOR CASTING
        // Horizontal ray components are shared by all pixels in a column.
        for (int x = 0; x < width; x++) {
            double cameraX = 2.0 * x / width - 1;
            double rayDirX = camera.xDir + camera.xPlane * cameraX;
            double rayDirY = camera.yDir + camera.yPlane * cameraX;
            fogHorizontalRayLengthsSquared[x] = rayDirX * rayDirX + rayDirY * rayDirY;
        }
        for(int y = 0; y < height; y++)
        {
            // whether this section is floor or ceiling
            boolean isFloor = y > height / 2 + pitch;

            // rayDir for leftmost ray (x = 0) and rightmost ray (x = w)
            float rayDirX0 = (float)(camera.xDir - camera.xPlane);
            float rayDirY0 = (float)(camera.yDir - camera.yPlane);
            float rayDirX1 = (float)(camera.xDir + camera.xPlane);
            float rayDirY1 = (float)(camera.yDir + camera.yPlane);

            // Current y position compared to the center of the screen (the horizon)
            double p = isFloor ? y - height / 2 - pitch : height / 2 - y + pitch;
            if (p == 0) {
                // The horizon is infinitely far away; avoid infinite texture coordinates.
                if (!skipWalls)
                    Arrays.fill(pixels, y * width, (y + 1) * width, fog.getColor());
                else {
                    for (int x = 0; x < width; x++)
                        if (y < wallDrawStarts[x] || y >= wallDrawEnds[x])
                            pixels[y * width + x] = fog.getColor();
                }
                continue;
            }

            // Vertical position of the camera.
            // NOTE: with 0.5, it's exactly in the center between floor and ceiling,
            // matching also how the walls are being raycasted. For different values
            // than 0.5, a separate loop must be done for ceiling and floor since
            // they're no longer symmetrical.
            float camZ = isFloor ? (float)(0.5 * height + posZ) : (float)(0.5 * height - posZ);

            // Horizontal distance from the camera to the floor for the current row.
            // 0.5 is the z position exactly in the middle between floor and ceiling.
            // NOTE: this is affine texture mapping, which is not perspective correct
            // except for perfectly horizontal and vertical surfaces like the floor.
            // NOTE: this formula is explained as follows: The camera ray goes through
            // the following two points: the camera itself, which is at a certain
            // height (posZ), and a point in front of the camera (through an imagined
            // vertical plane containing the screen pixels) with horizontal distance
            // 1 from the camera, and vertical position p lower than posZ (posZ - p). When going
            // through that point, the line has vertically traveled by p units and
            // horizontally by 1 unit. To hit the floor, it instead needs to travel by
            // posZ units. It will travel the same ratio horizontally. The ratio was
            // 1 / p for going through the camera plane, so to go posZ times farther
            // to reach the floor, we get that the total horizontal distance is posZ / p.
            double rowDistance = camZ / p;

            // calculate the real world step vector we have to add for each x (parallel to camera plane)
            // adding step by step avoids multiplications with a weight in the inner loop
            double floorStepX = rowDistance * (rayDirX1 - rayDirX0) / width;
            double floorStepY = rowDistance * (rayDirY1 - rayDirY0) / width;

            // real world coordinates of the leftmost column. This will be updated as we step to the right.
            double floorX = camera.xPos + rowDistance * rayDirX0;
            double floorY = camera.yPos + rowDistance * rayDirY0;

            for(int x = 0; x < width; ++x)
            {
                if (skipWalls && y >= wallDrawStarts[x] && y < wallDrawEnds[x]) {
                    floorX += floorStepX;
                    floorY += floorStepY;
                    continue;
                }
                // the cell coord is simply got from the integer parts of floorX and floorY
                int cellX = (int)(floorX);
                int cellY = (int)(floorY);

                // get the texture coordinate from the fractional part
                int tx = (int)(textureSize * (floorX - cellX)) & (textureSize - 1);
                int ty = (int)(textureSize * (floorY - cellY)) & (textureSize - 1);

                floorX += floorStepX;
                floorY += floorStepY;

                int color;

                // ceiling
                if(isFloor){
                    color = textures.get(ceilingTexture).pixels[textureSize * ty + tx];
                }
                else{
                    color = textures.get(floorTexture).pixels[textureSize * ty + tx];
                }

                color = (color >> 1) & 8355711;


                color = applyFogAtPixel(color, rowDistance, fogHorizontalRayLengthsSquared[x], y);

                pixels[y * width + x] = color;

            }
        }
        return pixels;
    }

    public int[] updateSprites(Camera camera, int[] pixels, int currentLevel){
        //SPRITE CASTING

        setNumberOfSprites(currentLevel);
        ArrayList<Entity> currentLevelSprites = allLevelSprites.get(currentLevel);

        //sort sprites from far to close
        for(int i = 0; i < numberOfSprites; i++){
            spriteOrder[i] = i;
            spriteDistance[i] = ((camera.xPos - currentLevelSprites.get(i).getXLoc()) * (camera.xPos - currentLevelSprites.get(i).getXLoc()) +
                    (camera.yPos - currentLevelSprites.get(i).getYLoc()) * (camera.yPos - currentLevelSprites.get(i).getYLoc()));
        }

        sortSprites(spriteOrder, spriteDistance, numberOfSprites, currentLevelSprites);

        //after sorting the sprites, do the projection and draw them
        for(int i = 0; i < numberOfSprites; i++){

            Entity currRenderSprite = currentLevelSprites.get(spriteOrder[i]);

            double spriteX = currRenderSprite.getXLoc() - camera.xPos;
            double spriteY = currRenderSprite.getYLoc() - camera.yPos;

            //calculate the difference between player and object position
            double dx = (camera.xPos - currRenderSprite.getXLoc());
            double dy = -(camera.yPos - currRenderSprite.getYLoc());

            //get the angle in radians and convert to 0-360 degrees
            double angle = Math.atan2(dx, dy);
            angle = Math.toDegrees(angle) + 180;

            currRenderSprite.setAngle(angle);

            if(currRenderSprite.isMultiSided()){
                currRenderSprite.setSpriteAccordingToAngle();
            }


            //transform sprite with the inverse camera matrix
            // [ planeX   dirX ] -1                                       [ dirY      -dirX ]
            // [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
            // [ planeY   dirY ]                                          [ -planeY  planeX ]

            double invDet = 1.0 / (camera.xPlane * camera.yDir - camera.xDir * camera.yPlane);

            double transformX = invDet * (camera.yDir * spriteX - camera.xDir * spriteY);
            double transformY = invDet * (-camera.yPlane * spriteX + camera.xPlane * spriteY); //this is actually the depth inside the screen, that what Z is in 3D

            int spriteScreenX = (int)((width / 2) * (1 + transformX / transformY));

            int uDiv = 3;
            int vDiv = 3;
            double vMove = currRenderSprite.getZPos();
            int vMoveScreen = (int)((vMove / transformY) + pitch + posZ / transformY);


            //calculate height of the sprite on screen
            int spriteHeight = Math.abs((int)(height / (transformY))) / vDiv;

            //calculate lowest and highest pixel to fill in current sprite
            int drawStartY = -spriteHeight / 2 + height / 2 + vMoveScreen;
            if(drawStartY < 0 )
                drawStartY = 0;

            int drawEndY = spriteHeight / 2 + height / 2 + vMoveScreen;
            if(drawEndY >= height)
                drawEndY = height - 1;

            //calculate width of the sprite
            int spriteWidth = Math.abs((int)(height / (transformY))) / uDiv;

            int drawStartX = -spriteWidth / 2 + spriteScreenX;
            if(drawStartX < 0)
                drawStartX = 0;

            int drawEndX = spriteWidth / 2 + spriteScreenX;
            if(drawEndX >= width)
                drawEndX = width - 1;

            //loop through every vertical stripe of the sprite on screen
            for(int stripe = drawStartX; stripe < drawEndX; stripe++){
                int spriteTexX = (int)(256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * currRenderSprite.getSpriteWidth() / spriteWidth) / 256;
                //the conditions in the if are:
                //1) it's in front of camera plane so you don't see things behind you
                //2) it's on the screen (left)
                //3) it's on the screen (right)
                //4) ZBuffer, with perpendicular distance
                if(transformY > 0 && stripe > 0 && stripe < width && transformY < ZBuffer[stripe]){
                    // Each billboard pixel uses the same 3D ray as walls and floors.
                    double cameraX = 2.0 * stripe / width - 1;
                    double rayDirX = camera.xDir + camera.xPlane * cameraX;
                    double rayDirY = camera.yDir + camera.yPlane * cameraX;
                    double horizontalRayLengthSquared = rayDirX * rayDirX + rayDirY * rayDirY;
                    for(int y  = drawStartY; y < drawEndY; y++){//for every pixel of the current stripe
                        int d = (y - vMoveScreen) * 256 - height * 128 + spriteHeight * 128;
                        int spriteTexY =((d * currRenderSprite.getSpriteHeight()) / spriteHeight) / 256;
                        int color = currRenderSprite.pixels[currentLevelSprites.get(spriteOrder[i]).getSpriteWidth() * spriteTexY + spriteTexX];
                        // Black texture pixels are transparent, even with coloured fog.
                        if((color & 0x00FFFFFF) != 0)
                            pixels[stripe + y * (width)] = applyFogAtPixel(color,
                                    transformY, horizontalRayLengthSquared, y);
                    }
                }
            }
        }
        return pixels;
    }

    private int applyFogAtPixel(int color, double depth, double horizontalRayLengthSquared, int y) {
        // Vertical projection uses height as its focal length. Recover the ray's
        // vertical component in map units; posZ is already part of surface projection.
        double verticalSlope = (height / 2 + pitch - y) / height;
        double distanceSquared = depth * depth * (horizontalRayLengthSquared + verticalSlope * verticalSlope);
        return fog.applyAtSquaredDistance(color, distanceSquared);
    }

    private void sortSprites(int[] order, double[] dist, int amount, ArrayList<Entity> sprites){
        for(int i = 0; i < amount; i++){
            sprites.get(i).setDistance(dist[i]);
            sprites.get(i).setOrder(order[i]);
        }
        Collections.sort(sprites, Entity.renderOrder);
        for(int i = 0; i < amount; i++){
            dist[i] = sprites.get(amount - i - 1 ).getDistance();
            order[i] = sprites.get(amount - i - 1).getOrder();
        }
    }

}
