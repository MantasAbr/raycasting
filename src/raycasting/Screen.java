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
     * to the Game class. The method begins by "clearing" the screen. 
     * It does this by setting all of the pixels on the top half to one color 
     * and all of the pixels on the bottom to another.
     * @param camera
     * @param pixels
     * @return 
     */
    public int[] update(Camera camera, int[] pixels){
        for(int n = 0; n < pixels.length / 2; n++)
            if(pixels[n] != Color.DARK_GRAY.getRGB())
                pixels[n] = Color.DARK_GRAY.getRGB();
        
        for(int i = pixels.length / 2; i < pixels.length; i++)
            if(pixels[i] != Color.GRAY.getRGB())
                pixels[i] = Color.GRAY.getRGB();
        
        for(int x = 0; x < width; x = x + 1){            
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
            int side=0;
            
            
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
            if(side==1) {//If its a y-axis wall
                wallX = (camera.xPos + ((mapY - camera.yPos + (1 - stepY) / 2) / rayDirY) * rayDirX);
            } else {//X-axis wall
                wallX = (camera.yPos + ((mapX - camera.xPos + (1 - stepX) / 2) / rayDirX) * rayDirY);
            }
            wallX-=Math.floor(wallX);
            //x coordinate on the texture
            int texX = (int)(wallX * (textures.get(texNum).SIZE));
            if(side == 0 && rayDirX > 0) texX = textures.get(texNum).SIZE - texX - 1;
            if(side == 1 && rayDirY < 0) texX = textures.get(texNum).SIZE - texX - 1;
                       
            //calculate y coordinate on texture
            for(int y=drawStart; y<drawEnd; y++) {
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
