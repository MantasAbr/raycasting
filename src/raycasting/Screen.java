package raycasting;
import java.util.ArrayList;
import java.awt.Color;

/**
 *
 * @author Mantas Abramavičius
 */
public class Screen {
    
    public int[][] map;
    public int[][] doorMap;
    public int mapWidth, mapHeight, width, height;
    public ArrayList<Texture> textures;
    public ArrayList<Sprite> sprites;
    public double distanceToWall;
    public int lookingAtTextureId;
    public int lookingAtMeshId;
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
    
    public Screen(int[][] map, int[][] doorMap, int mapWidth, int mapHeight, ArrayList<Texture> textures, ArrayList<Sprite> sprites, int width, int height, int renderDistance){
        this.map = map;
        this.doorMap = doorMap;
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

    public void setMap(int[][] newMap){
        this.map = newMap;
    }

    public void setDoorMap(int[][] newMap){
        this.doorMap = newMap;
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

        //FLOOR CASTING
        for(int y = 0; y < height; y++)
        {
            // rayDir for leftmost ray (x = 0) and rightmost ray (x = w)
            float rayDirX0 = (float)(camera.xDir - camera.xPlane);
            float rayDirY0 = (float)(camera.yDir - camera.yPlane);
            float rayDirX1 = (float)(camera.xDir + camera.xPlane);
            float rayDirY1 = (float)(camera.yDir + camera.yPlane);

            // Current y position compared to the center of the screen (the horizon)
            int p = y - height / 2;

            // Vertical position of the camera.
            float posZ = (float)(0.5 * height);

            // Horizontal distance from the camera to the floor for the current row.
            // 0.5 is the z position exactly in the middle between floor and ceiling.
            float rowDistance = posZ / p;

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
                color = textures.get(ceilingTexture).pixels[textureSize * ty + tx];
                color = (color >> 1) & 8355711; // make a bit darker
                pixels[y * width + x] = color;

                //floor (symmetrical, at screenHeight - y - 1 instead of y)
                color = textures.get(floorTexture).pixels[textureSize * ty + tx];
                color = (color >> 1) & 8355711; // make a bit darker
                pixels[(height-y - 1)*width+x] = color;
            }
        }

        
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

            ZBuffer[x] = perpWallDist;

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
            int drawStart = -lineHeight / 2 + height / 2;
            if(drawStart < 0)
                drawStart = 0;
            int drawEnd = lineHeight / 2 + height / 2;
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

            //calculate y coordinate on texture

            for(int y = drawStart; y < drawEnd; y++) {
                int texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
                int color;
                if(side == 0)
                    color = textures.get(texNum).pixels[texX + (texY * textureSize)];
                else
                    color = (textures.get(texNum).pixels[texX + (texY * textureSize)]>>1) & 8355711;//Make y sides darker

                wallBuffer[x + y*(width)] = perpWallDist;

//                    if(perpWallDist > renderDistance){
//                        //renderDistanceLimiterNew(pixels, ZBuffer);
//                        pixels[x + y*(width)] = 0;
//                    }
//                    else
                        pixels[x + y*(width)] = color;
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
        for(int i = 0; i < width; i++){
            int color = pixels[i];
            int brightness = (int) (Raycasting.RENDER_DISTANCE / (buffer[i]));

            if(brightness < 0)
                brightness = 0;
            if(brightness > 255)
                brightness = 255;

            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color) & 0xff;

            r = r * brightness >>> 8;
            g = g * brightness >>> 8;
            b = b * brightness >>> 8;

            color = r << 16 | g << 8 | b;
        }
    }

    private void renderDistanceLimiterNew(int pixels[], double[] buffer){
        for(int i = 0; i < width; i++){
            int color = pixels[i];

            int r = (color >> 16) & 0xff;
            int g = (color >> 8) & 0xff;
            int b = (color) & 0xff;

            Color darker = new Color(r, g, b);
            darker.darker();

            pixels[i] = darker.getRGB();
        }
    }
}
