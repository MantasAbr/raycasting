package raycasting;
import sprites.GameSprite;
import sprites.Sprite;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.awt.Color;
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
    public ArrayList<ArrayList<GameSprite>> allLevelSprites;
    public double distanceToWall;
    public int lookingAtTextureId;
    public int lookingAtMeshId;
    public double rayX, rayY;
    public int renderDistance;

    public double pitch = 0;
    public double posZ = 0;

    private final int textureSize = 64;

    private int ceilingTexture = 2;
    private int floorTexture = 0;

    private double wallBuffer[];
    private double floorBuffer[];

    private int numberOfSprites;
    private double ZBuffer[];
    private int spriteOrder[];
    private double spriteDistance[];
    
    public Screen(int[][] map, int[][] doorMap, int mapWidth, int mapHeight, ArrayList<Texture> textures,
                  ArrayList<ArrayList<GameSprite>> allLevelSprites, int currentLevel, int width, int height, int renderDistance){
        this.map = map;
        this.doorMap = doorMap;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.textures = textures;
        this.allLevelSprites = allLevelSprites;
        this.width = width;
        this.height = height;
        this.renderDistance = renderDistance;

        wallBuffer = new double[width * height];
        floorBuffer = new double[width * height];

        ZBuffer = new double[width];
        numberOfSprites = allLevelSprites.get(currentLevel).size();
        spriteOrder = new int[numberOfSprites];
        spriteDistance = new double[numberOfSprites];
    }

    public void setMap(int[][] newMap){
        this.map = newMap;
    }

    public void setDoorMap(int[][] newMap){
        this.doorMap = newMap;
    }

    public void setNumberOfSprites(int currentLevel){
        numberOfSprites = allLevelSprites.get(currentLevel).size();
    }


    public int[] updateWalls(Camera camera, int[] pixels){
        for(int x = 0; x < width; x++){            
            /**
             * All that happens here is some variables that will be used by
             * the rest of the loop are calculated. 
             * CameraX is the x-coordinate of the current vertical stripe
             * on the camera plane, and the rayDir variables make a vector for 
             * the ray. All of the variables ending in DistX or DistY 
             * are calculated so that the program only checks for collisions 
             * at the places where collisions could possibly occur. 
             * perpWallDist is the distance from the player to the first wall 
             * the ray collides with.
             */
            
            //WALL CASTING
            
            double cameraX = 2 * x / (double)(width) - 1;
            double rayDirX = camera.xDir + camera.xPlane * cameraX;
            double rayDirY = camera.yDir + camera.yPlane * cameraX;

            //Map position
            int mapX = (int)camera.xPos;
            int mapY = (int)camera.yPos;

            //length of ray from current position to next x or y-side
            double sideDistX;
            double sideDistY;

            //Length of ray from one side to next in map
            double deltaDistX = Math.sqrt(1 + (rayDirY*rayDirY) / (rayDirX*rayDirX));
            double deltaDistY = Math.sqrt(1 + (rayDirX*rayDirX) / (rayDirY*rayDirY));
            double perpWallDist;

            //Direction to go in x and y
            int stepX, stepY;
            //was a wall hit
            boolean hit = false;
            //was the wall vertical or horizontal
            int side = 0;


            //Figure out the step direction and initial distance to a side
            if (rayDirX < 0)
            {
                stepX = -1;
                sideDistX = (camera.xPos - mapX) * deltaDistX;
            }
            else
            {
                stepX = 1;
                sideDistX = (mapX + 1.0 - camera.xPos) * deltaDistX;
            }
            if (rayDirY < 0)
            {
                stepY = -1;
                sideDistY = (camera.yPos - mapY) * deltaDistY;
            }
            else
            {
                stepY = 1;
                sideDistY = (mapY + 1.0 - camera.yPos) * deltaDistY;
            }


            //Loop to find where the ray hits a wall
            while(!hit) {
                //Jump to next square
                if (sideDistX < sideDistY)
                {
                    sideDistX += deltaDistX;
                    mapX += stepX;
                    side = 0;
                }
                else
                {
                    sideDistY += deltaDistY;
                    mapY += stepY;
                    side = 1;
                }
                //Check if ray has hit a wall
                if(map[mapX][mapY] > 0) hit = true;
            }


            //Calculate distance to the point of impact
            if(side==0)
                perpWallDist = Math.abs((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX);
            else
                perpWallDist = Math.abs((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY);

            //Gets the ID of the block that is currently being viewed and the distance to it
            if(x <= width / 2){
                lookingAtTextureId = map[mapX][mapY];
                lookingAtMeshId = doorMap[mapX][mapY];
                distanceToWall = perpWallDist;
                rayX = rayDirX;
                rayY = rayDirY;
            }

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


            //add a texture
            int texNum = map[mapX][mapY] - 1;
            double wallX;//Exact position of where wall was hit
            if(side == 1) {//If its a y-axis wall
                wallX = (camera.xPos + ((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY) * rayDirX);
            }
            else {//X-axis wall
                wallX = (camera.yPos + ((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX) * rayDirY);
            }
            wallX -= Math.floor(wallX);
            //x coordinate on the texture
            int texX = (int)(wallX * (textureSize));
            if(side == 0 && rayDirX > 0) texX = textureSize - texX - 1;
            if(side == 1 && rayDirY < 0) texX = textureSize - texX - 1;

            double step = 1.0 * textureSize / lineHeight;
            double texPos = (drawStart - pitch - (posZ / perpWallDist) - height / 2 + lineHeight / 2) * step;


            //calculate y coordinate on texture

            for(int y = drawStart; y < drawEnd; y++) {
                //int texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
                int texY = (int)texPos & (textureSize - 1);
                texPos += step;
                int color;
                if(side == 0)
                    color = textures.get(texNum).pixels[texX + (texY * textureSize)];
                else
                    color = (textures.get(texNum).pixels[texX + (texY * textureSize)]>>1) & 8355711;//Make y sides darker

                wallBuffer[x + y*(width)] = perpWallDist;

                color = setPixelColorForRenderLimiter(color, perpWallDist, true);
                pixels[x + y*(width)] = color;
            }

            ZBuffer[x] = perpWallDist;

        }
        return pixels;
    }

    public int[] updateFloorAndCeiling(Camera camera, int[] pixels){
        //FLOOR CASTING
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
            int p = isFloor ? (int)(y - height / 2 - pitch) : (int)(height / 2 - y + pitch);

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
            float rowDistance = camZ / p;

            // calculate the real world step vector we have to add for each x (parallel to camera plane)
            // adding step by step avoids multiplications with a weight in the inner loop
            float floorStepX = rowDistance * (rayDirX1 - rayDirX0) / width;
            float floorStepY = rowDistance * (rayDirY1 - rayDirY0) / width;

            // real world coordinates of the leftmost column. This will be updated as we step to the right.
            float floorX = (float)(camera.xPos + rowDistance * rayDirX0);
            float floorY = (float)(camera.yPos + rowDistance * rayDirY0);

            for(int x = 0; x < width; ++x)
            {
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


                color = setPixelColorForRenderLimiter(color, rowDistance, false);

                pixels[y * width + x] = color;

            }
        }
        return pixels;
    }

    public int[] updateSprites(Camera camera, int[] pixels, int currentLevel){
        //SPRITE CASTING

        setNumberOfSprites(currentLevel);
        ArrayList<GameSprite> currentLevelSprites = allLevelSprites.get(currentLevel);

        //sort sprites from far to close
        for(int i = 0; i < numberOfSprites; i++){
            spriteOrder[i] = i;
            spriteDistance[i] = ((camera.xPos - currentLevelSprites.get(i).getXLoc()) * (camera.xPos - currentLevelSprites.get(i).getXLoc()) +
                    (camera.yPos - currentLevelSprites.get(i).getYLoc()) * (camera.yPos - currentLevelSprites.get(i).getYLoc()));
        }

        sortSprites(spriteOrder, spriteDistance, numberOfSprites, currentLevelSprites);

        //after sorting the sprites, do the projection and draw them
        for(int i = 0; i < numberOfSprites; i++){


            double spriteX = currentLevelSprites.get(spriteOrder[i]).getXLoc() - camera.xPos;
            double spriteY = currentLevelSprites.get(spriteOrder[i]).getYLoc() - camera.yPos;

            double angle = Math.atan2((camera.xPos - spriteX), (camera.xPos - spriteY));

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
            double vMove = currentLevelSprites.get(i).getZPos();
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
                int spriteTexX = (int)(256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * currentLevelSprites.get(spriteOrder[i]).getSpriteWidth() / spriteWidth) / 256;
                //the conditions in the if are:
                //1) it's in front of camera plane so you don't see things behind you
                //2) it's on the screen (left)
                //3) it's on the screen (right)
                //4) ZBuffer, with perpendicular distance
                if(transformY > 0 && stripe > 0 && stripe < width && transformY < ZBuffer[stripe]){
                    for(int y  = drawStartY; y < drawEndY; y++){//for every pixel of the current stripe
                        int d = (y - vMoveScreen) * 256 - height * 128 + spriteHeight * 128;
                        int spriteTexY =((d * currentLevelSprites.get(spriteOrder[i]).getSpriteHeight()) / spriteHeight) / 256;
                        int color = currentLevelSprites.get(spriteOrder[i]).pixels[currentLevelSprites.get(spriteOrder[i]).getSpriteWidth() * spriteTexY + spriteTexX];
                        color = setPixelColorForRenderLimiter(color, transformY, false);
                        if((color & 0x00FFFFFF) != 0)
                            pixels[stripe + y * (width)] = color;
                    }
                }
            }
        }
        return pixels;
    }

    private void sortSprites(int[] order, double[] dist, int amount, ArrayList<GameSprite> sprites){
        for(int i = 0; i < amount; i++){
            sprites.get(i).setDistance(dist[i]);
            sprites.get(i).setOrder(order[i]);
        }
        Collections.sort(sprites, GameSprite.renderOrder);
        for(int i = 0; i < amount; i++){
            dist[i] = sprites.get(amount - i - 1 ).getDistance();
            order[i] = sprites.get(amount - i - 1).getOrder();
        }
    }

    private int setPixelColorForRenderLimiter(int color, double distance, boolean isWall){

        int r = (color >> 16) & 0xff;
        int g = (color >> 8) & 0xff;
        int b = (color) & 0xff;

        if(!isWall){
            if(distance > 10)
                distance = 10;
        }

        if(distance > Raycasting.RENDER_DISTANCE){
            for(int i = 0; i < (int)distance - Raycasting.RENDER_DISTANCE; i++){
                r = r >>> i;
                g = g >>> i;
                b = b >>> i;
            }
        }

        color = r << 16 | g << 8 | b;
        return color;
    }
}
