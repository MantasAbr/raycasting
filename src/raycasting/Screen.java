package raycasting;
import java.util.ArrayList;
import java.awt.Color;

/**
 *
 * @author Mantas Abramavičius
 */
public class Screen {
    
    public int[][] map;
    public int mapWidth, mapHeight, width, height;
    public ArrayList<Texture> textures;
    public ArrayList<Sprite> sprites;
    public double distanceToWall;
    public int lookingAtTextureId;
    public double rayX, rayY;
    public int renderDistance;

    private int ceilingTexture = 2;
    private int floorTexture = 0;

    private double wallBuffer[];
    private double floorBuffer[];

    private int numberOfSprites;
    private double ZBuffer[];
    private int spriteOrder[];
    private double spriteDistance[];
    
    public Screen(int[][] map, int mapWidth, int mapHeight, ArrayList<Texture> textures, ArrayList<Sprite> sprites, int width, int height, int renderDistance){
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.textures = textures;
        this.sprites = sprites;
        this.width = width;
        this.height = height;
        this.renderDistance = renderDistance;

        wallBuffer = new double[width * height];
        floorBuffer = new double[width * height];

        ZBuffer = new double[width];
        numberOfSprites = sprites.size();
        spriteOrder = new int[numberOfSprites];
        spriteDistance = new double[numberOfSprites];
    }
    
    /**
     * The update method recalculates how the screen should look to the user 
     * based on their position in the map. 
     * The method is called constantly, and returns the updated array of pixels 
     * to the Raycasting class. The method begins by "clearing" the screen.
     * @param camera
     * @param pixels
     * @return 
     */
    public int[] update(Camera camera, int[] pixels){
        
        int textureSize = textures.get(0).SIZE;
        
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
            int drawStart = -lineHeight / 2 + height / 2;
            if(drawStart < 0)
                drawStart = 0;
            int drawEnd = lineHeight / 2 + height / 2;
            if(drawEnd >= height) 
                drawEnd = height - 1;

            ZBuffer[x] = perpWallDist;
            
            
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
                       
            //calculate y coordinate on texture
            for(int y = drawStart; y < drawEnd; y++) {
                int texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
                int color;
                if(side == 0) 
                    color = textures.get(texNum).pixels[texX + (texY * textureSize)];
                else 
                    color = (textures.get(texNum).pixels[texX + (texY * textureSize)]>>1) & 8355711;//Make y sides darker

                //wallBuffer[x + y*(width)] = perpWallDist;

                if(perpWallDist > renderDistance){
                    //renderDistanceLimiter(pixels, ZBuffer);
                    pixels[x + y*(width)] = 0;
                }
                else
                    pixels[x + y*(width)] = color;
            }


                                   
            //FLOOR AND CEILING CASTING
            
            double floorXWall, floorYWall;
            
            //4 different wall directions possible
            if(side == 0 && rayDirX > 0)
            {
              floorXWall = mapX;
              floorYWall = mapY + wallX;
            }
            else if(side == 0 && rayDirX < 0)
            {
              floorXWall = mapX + 1.0;
              floorYWall = mapY + wallX;
            }
            else if(side == 1 && rayDirY > 0)
            {
              floorXWall = mapX + wallX;
              floorYWall = mapY;
            }
            else
            {
              floorXWall = mapX + wallX;
              floorYWall = mapY + 1.0;
            }
            
            double distWall, distPlayer, currentDist;

            distWall = perpWallDist;
            distPlayer = 0.0;
            
            if (drawEnd < 0) drawEnd = height;
            

            
            for(int y = drawEnd + 1; y < height; y++){
                currentDist = height / (2.0 * y - height);
                
                double weight = (currentDist - distPlayer) / (distWall - distPlayer);
                
                double currentFloorX = weight * floorXWall + (1.0 - weight) * camera.xPos;
                double currentFloorY = weight * floorYWall + (1.0 - weight) * camera.yPos;
                
                int floorTexX, floorTexY;
                floorTexX = (int)(currentFloorX * textureSize / 2) % textureSize;
                floorTexY = (int)(currentFloorY * textureSize / 2) % textureSize;

                //floorBuffer[x + y*(width)] = currentDist;

                if(currentDist > renderDistance){
                    //renderDistanceLimiter(pixels, floorBuffer);
                    pixels[x + y*(width)] = 0;
                    pixels[(height-y)*width+x] = 0;

                }
                else{
                    pixels[x + y*(width)] = (textures.get(ceilingTexture).pixels[textureSize * floorTexY + floorTexX] >> 1) & 8355711;
                    pixels[(height-y)*width+x] = (textures.get(floorTexture).pixels[textureSize * floorTexY + floorTexX]);
                }
            }

            //SPRITE CASTING

            //sort sprites from far to close
            for(int i = 0; i < numberOfSprites; i++){
                spriteOrder[i] = i;
                spriteDistance[i] = ((camera.xPos - sprites.get(i).getXLoc()) * (camera.xPos - sprites.get(i).getXLoc()) +
                                     (camera.yPos - sprites.get(i).getYLoc()) * (camera.yPos - sprites.get(i).getYLoc()));
            }

            sortSprites(spriteOrder, spriteDistance, numberOfSprites);

            //after sorting the sprites, do the projection and draw them
            for(int i = 0; i < numberOfSprites; i++){

                double spriteX = sprites.get(spriteOrder[i]).getXLoc() - camera.xPos;
                double spriteY = sprites.get(spriteOrder[i]).getYLoc() - camera.yPos;

                //transform sprite with the inverse camera matrix
                // [ planeX   dirX ] -1                                       [ dirY      -dirX ]
                // [               ]       =  1/(planeX*dirY-dirX*planeY) *   [                 ]
                // [ planeY   dirY ]                                          [ -planeY  planeX ]

                double invDet = 1.0 / (camera.xPlane * camera.yDir - camera.xDir * camera.yPlane);

                double transformX = invDet * (camera.yDir * spriteX - camera.xDir * spriteY);
                double transformY = invDet * (-camera.yPlane * spriteX + camera.xPlane * spriteY); //this is actually the depth inside the screen, that what Z is in 3D

                int spriteScreenX = (int)((width / 2) * (1 + transformX / transformY));

                //calculate height of the sprite on screen
                int spriteHeight = Math.abs((int)(height / (transformY)));

                //calculate lowest and highest pixel to fill in current sprite
                int drawStartY = -spriteHeight / 2 + height / 2;
                if(drawStartY < 0 )
                    drawStartY = 0;

                int drawEndY = spriteHeight / 2 + height / 2;
                if(drawEndY >= height)
                    drawEndY = height - 1;

                //calculate width of the sprite
                int spriteWidth = Math.abs((int)(height / (transformY)));

                int drawStartX = -spriteWidth / 2 + spriteScreenX;
                if(drawStartX < 0)
                    drawStart = 0;

                int drawEndX = spriteWidth / 2 + spriteScreenX;
                if(drawEndX >= width)
                    drawEndX = width - 1;

                //loop through every vertical stripe of the sprite on screen
                for(int stripe = drawStartX; stripe < drawEndX; stripe++){
                    int spriteTexX = (int)(256 * (stripe - (-spriteWidth / 2 + spriteScreenX)) * sprites.get(i).getSpriteWidth() / spriteWidth) / 256;
                    //the conditions in the if are:
                    //1) it's in front of camera plane so you don't see things behind you
                    //2) it's on the screen (left)
                    //3) it's on the screen (right)
                    //4) ZBuffer, with perpendicular distance
                    if(transformY > 0 && stripe > 0 && stripe < width && transformY < ZBuffer[stripe]){
                        for(int y  = drawStartY; y < drawEndY; y++){//for every pixel of the current stripe
                            int d = (y) * 256 - height * 128 + spriteHeight * 128;
                            int spriteTexY =((d * sprites.get(i).getSpriteHeight()) / spriteHeight) / 256;
                            int color = sprites.get(spriteOrder[i]).pixels[sprites.get(spriteOrder[i]).getSpriteWidth() * spriteTexY + spriteTexX];
                            if((color & 0x00FFFFFF) != 0);
                                pixels[stripe + y * (width)] = color;
                        }
                    }
                }
            }
        }
        return pixels;
    }

    private void sortSprites(int[] order, double[] dist, int amount){

    }

    /**
     * The method to be used for creating a smooth transition to black pixels. Not called - doesn't work as of yet
     * @param pixels
     * @param buffer
     */
    private void renderDistanceLimiter(int pixels[], double buffer[]){
        for(int i = 0; i < width * height; i++){
            int color = pixels[i];
            int brightness = (int) (5 / (buffer[i]));

            if(brightness < 0)
                brightness = 0;
            if(brightness > 255)
                brightness = 255;

            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color) & 0xff;

            r = r * brightness / 255;
            g = g * brightness / 255;
            b = b * brightness / 255;

            pixels[i] = r << 16 | g << 8 | b;
        }
    }
}
