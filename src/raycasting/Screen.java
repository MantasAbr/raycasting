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
    public double distanceToWall;
    public int lookingAtTextureId;
    public double rayX, rayY;
    
    public Screen(int[][] map, int mapWidth, int mapHeight, ArrayList<Texture> textures, int width, int height){
        this.map = map;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.textures = textures;
        this.width = width;
        this.height = height;
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
        
        //Floor casting
        for(int y = 0; y < height; y++){
            
            // RayDir for leftmost ray (x = 0) and rightmost ray (x = w)
            float rayDirX0 = (float)(camera.xDir - camera.xPlane);
            float rayDirY0 = (float)(camera.yDir - camera.yPlane);
            float rayDirX1 = (float)(camera.xDir + camera.xPlane);
            float rayDirY1 = (float)(camera.yDir + camera.yPlane);
            
            
            // Current y position compared to the center of the screen (horizon)
            int p = y - height / 2;
            
            // Vertical position of the camera
            float posZ = (float)0.5 * height;
            
            // Horizontal distance from the camera to the floor for the current row.
            // 0.5 is the z position exactly in the middle between floor and ceiling.
            float rowDistance = posZ / p;
            
            // calculate the real world step vector we have to add for each x (parallel to camera plane)
            // adding step by step avoids multiplications with a weight in the inner loop
            float floorStepX = rowDistance * (rayDirX1 - rayDirX0) / width;
            float floorStepY = rowDistance * (rayDirY1 - rayDirY0) / width;
            
            // real world coordinates of the leftmost column. This will be updated as we step to the right.
            float floorX = (float)camera.xPos + rowDistance * rayDirX0;
            float floorY = (float)camera.yPos + rowDistance * rayDirY0;
            
            for(int x = 0; x < width; x++){
                
                // the cell coord is simply got from the integer parts of floorX and floorY
                int cellX = (int)(floorX);
                int cellY = (int)(floorY);
                
                int floorTexture = 2;
                int ceilingTexture = 0;
                int color;
                                
                // get the texture coordinate from the fractional part
                int tx = (int)(textures.get(0).SIZE * (floorX - cellX)) & (textures.get(0).SIZE - 1);
                int ty = (int)(textures.get(0).SIZE * (floorY - cellY)) & (textures.get(0).SIZE - 1);

                floorX += floorStepX;
                floorY += floorStepY;
               
                color = textures.get(floorTexture).pixels[tx + (ty * textures.get(floorTexture).SIZE)];
                color = (color >> 1) & 8355711;
                pixels[x + y*(width)] = color;
                
//                color = textures.get(ceilingTexture).pixels[tx + (ty * textures.get(ceilingTexture).SIZE)];
//                color = (color >> 1) & 8355711;
//                pixels[x + (y*(width) / height)] = color;
            }
        }
        
        for(int n = 0; n < pixels.length / 2; n++)
            if(pixels[n] != Color.DARK_GRAY.getRGB())
                pixels[n] = Color.DARK_GRAY.getRGB();
        
//        for(int i = pixels.length / 2; i < pixels.length; i++)
//            if(pixels[i] != Color.GRAY.getRGB())
//                pixels[i] = Color.GRAY.getRGB();
        
        //Wall casting
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
            if(perpWallDist > 0) lineHeight = Math.abs((int)(height / perpWallDist));
            else lineHeight = height;
            
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
            int texX = (int)(wallX * (textures.get(texNum).SIZE));
            if(side == 0 && rayDirX > 0) texX = textures.get(texNum).SIZE - texX - 1;
            if(side == 1 && rayDirY < 0) texX = textures.get(texNum).SIZE - texX - 1;
                       
            //calculate y coordinate on texture
            for(int y = drawStart; y < drawEnd; y++) {
                int texY = (((y * 2 - height + lineHeight) << 6) / lineHeight) / 2;
                int color;
                if(side == 0) 
                    color = textures.get(texNum).pixels[texX + (texY * textures.get(texNum).SIZE)];
                else 
                    color = (textures.get(texNum).pixels[texX + (texY * textures.get(texNum).SIZE)]>>1) & 8355711;//Make y sides darker
                pixels[x + y*(width)] = color;
            }           
        }
        return pixels;
    }
}
