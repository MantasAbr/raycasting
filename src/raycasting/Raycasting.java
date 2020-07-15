package raycasting;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import javax.swing.JFrame;

/**
 *
 * @author Mantas Abramavičius
 */
public class Raycasting extends JFrame implements Runnable{
    private static final long serialVersionUID = 1L;
    //The width and height of the map matrix
    public int mapWidth = 15;
    public int mapHeight = 15;
    
    //Used for the run() method
    private Thread thread;
    private boolean running;
    
    //Used for displaying the image
    private BufferedImage image;
    public int[] pixels;
    
    //ArrayList for all of the textures used
    public ArrayList<Texture> textures;
    
    //Objects declarations
    public Camera camera;
    public Screen screen;
    public ActionHandling actions;
    
    //used for showing the ticks and frames each second on the screen
    private int finalTicks = 0;
    private int finalFrames = 0;
    public static int[][] map =
        {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,5,6,6,5,5,5,7,5,5,5,6,6,5,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,5,0,0,0,5,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}   
        }; 
    
    public Raycasting(){
        thread = new Thread(this);
        image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        textureInit();
        camera = new Camera(2, 6, 1.2, 0, 0, -.66);
        screen = new Screen(map, mapWidth, mapHeight, textures, 640, 480);
        actions = new ActionHandling(camera, screen, map);
        addKeyListener(camera);
        
        jFrameInit();             
        start();
    }
    
    /**
     * Used to load all the textures
     */
    private void textureInit(){
        textures = new ArrayList<Texture>();
        textures.add(Texture.wood);
        textures.add(Texture.brick);
        textures.add(Texture.stone);
        textures.add(Texture.leaves);
        textures.add(Texture.woodBricks);
        textures.add(Texture.glass);
        textures.add(Texture.door);
    }
    
    private void jFrameInit(){
        setSize(640, 480);
        setResizable(false);
        setTitle("veri nice 3d engine yes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(Color.black);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private synchronized void start() {
        running = true;
        thread.start();
    }
    
    public synchronized void stop(){
        running = false;
        try {
            thread.join();
        }
        catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Buffer strategy is used so that the updates are smoother;
     * To actually draw the image to the screen, a graphics object is obtained
     * from the buffer strategy and used to draw our image
     */
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(2);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        if(camera.debug)
            debugInfo(g);
        bs.show();
    }
    
    public void debugInfo(Graphics g){
        Font font = new Font("Courier new", Font.BOLD ,16);
        g.setColor(Color.white);
        g.setFont(font);
        g.drawString(finalTicks + " ticks, " + finalFrames + " frames per second", 10, 50);
        g.drawString("X Position: " + String.format("%.3f", camera.xPos) + ", Y Position: " + String.format("%.3f", camera.yPos), 10, 70);
        g.drawString("Facing X: " + String.format("%.3f", screen.rayX) + ", Facing y: " + String.format("%.3f", screen.rayY), 10, 90);
        g.drawString("Distance to wall: " + String.format("%.3f", screen.distanceToWall) + ". Looking at texture ID: " + screen.lookingAtTextureId, 10, 110);
        g.drawString("Can open something?", 10, 130);
        g.drawString(actions.canOpen ? "Yes" : "No", 210, 130);
    }
    
    public void tick(){
        actions.CheckForActions();
    }

    /*
     * Updates every 1/60th of a second
     */  
    @Override
    public void run(){
        long lastTime = System.nanoTime();
        double NanosPerTick = 1000000000D / 60D;              
        int ticks = 0;
        int frames = 0;        
        long lastTimer = System.currentTimeMillis();
        double delta = 0;              
        
        while(true){
            long now = System.nanoTime();
            delta += (now - lastTime) / NanosPerTick;
            lastTime = now;
            boolean shouldRender = true;            
                
            while(delta >= 1){
                ticks++;
                tick();
                screen.update(camera, pixels);
                camera.update(map);    
                delta -= 1;
                shouldRender = true;
            }
                                
            try {
                //can increase the sleep rate to lower the fps
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            
            if(shouldRender){                         
                frames++;
                render();            
            }
            
            if(System.currentTimeMillis() - lastTimer >= 1000){
                lastTimer += 1000;
                finalTicks = ticks;
                finalFrames = frames;
                //System.out.println(ticks + " ticks, " + frames + " frames per second");               
                frames = 0;
                ticks = 0;                
            }
        }
    }

    public static void main(String [] args){
        Raycasting raycasting = new Raycasting();
    }
}
