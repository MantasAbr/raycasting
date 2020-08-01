package raycasting;
import java.awt.*;
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
    public static int WINDOW_WIDTH = 800;
    public static int WINDOW_HEIGHT = 600;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static final double MOUSE_SENSITIVITY = 125.5;
    
    //The width and height of the map matrix
    public int mapWidth = 15;
    public int mapHeight = 20;
    
    //Used for the run() method
    private Thread thread;
    private boolean running;
    
    //Used for displaying the image
    private BufferedImage image;
    public int[] pixels;
    
    //ArrayList for objects
    public ArrayList<Texture> textures;
    public ArrayList<Sounds> sounds;
    
    //Objects declarations
    public Camera camera;
    public Screen screen;
    public ActionHandling actions;
    public Robot robot;
    public GraphicsDevice gd;
    public UserInterface userInterface;
    public Player player;
    
    //used for showing the ticks and frames each second on the screen
    private int finalTicks = 0;
    private int finalFrames = 0;
    public static int[][] map =
        {
            {4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},
            {4,0,0,0,0,4,0,0,0,4,0,0,0,0,4},
            {4,0,0,0,0,4,0,0,0,4,0,0,0,0,4},
            {4,0,0,0,0,4,0,0,0,4,0,0,0,0,4},
            {4,0,0,0,0,5,0,0,0,5,0,0,0,0,4},
            {4,4,4,4,4,4,0,0,0,4,4,4,4,4,4},
            {4,0,0,0,0,4,0,0,0,4,0,0,0,0,4},
            {4,0,0,0,0,4,0,0,0,4,0,0,0,0,4},
            {4,0,0,0,0,4,0,0,0,4,0,0,0,0,4},
            {4,0,0,0,0,5,0,0,0,5,0,0,0,0,4},
            {4,0,0,0,0,4,0,0,0,4,0,0,0,0,4},
            {4,4,4,4,4,4,4,5,4,4,4,4,4,4,4},
            {4,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
            {4,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
            {4,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
            {4,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
            {4,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
            {4,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
            {4,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
            {4,4,4,4,4,4,4,4,4,4,4,4,4,4,4}   
        }; 
    
    public Raycasting(){
        thread = new Thread(this);
        image = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
        textureInit();
        audioInit();
        mouseInit();
        player = new Player(2, 7.5, 100, 100, .8);
        camera = new Camera(player.getXLocation(), player.getYLocation(), 1.2, 0, 0, -.66, sounds, this);
        screen = new Screen(map, mapWidth, mapHeight, textures, WINDOW_WIDTH, WINDOW_HEIGHT, 8);
        actions = new ActionHandling(camera, screen, this);
        userInterface = new UserInterface(player, camera);
        addKeyListener(camera);
        addMouseListener(camera);
        addMouseMotionListener(camera);
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
        textures.add(Texture.woodBricks);
        textures.add(Texture.door);
    }
    
    private void audioInit(){
        sounds = new ArrayList<Sounds>();
        sounds.add(Sounds.stoneWalk);
        sounds.add(Sounds.stoneRun);       
    }
    
    private void mouseInit(){
        BufferedImage cursor = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(0, 0), "blank");
        getContentPane().setCursor(blankCursor);
        try{
            robot = new Robot();
            gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            SCREEN_WIDTH = gd.getDisplayMode().getWidth();
            SCREEN_HEIGHT = gd.getDisplayMode().getHeight();
            robot.mouseMove(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2);
        }
        catch (AWTException e){
            System.exit(1);
        }
    }
    
    private void jFrameInit(){
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
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

        userInterface.DrawInterface(g);

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
        g.drawString("Facing block coords. X: " + actions.forwardBlockX + ", Y: " + actions.forwardBlockY, 10, 130);
        g.drawString("Sprint value: " + player.getSprintValue(), 10, 150);
    }
    
    public void tick(){
        actions.GetNextBlock();
        actions.CheckForActions();
        actions.ApplyBlockChanges(map);
        actions.mouseMovementHandling(MOUSE_SENSITIVITY);
        player.ApplyUpdates(camera);
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
            boolean shouldRender = false;
                
            while(delta >= 1){
                ticks++;
                tick();
                delta -= 1;
                shouldRender = true;
                screen.update(camera, pixels);
                camera.update(map);
            }
                                
            try {
                //can increase the sleep rate to lower the fps and processing power
                Thread.sleep(8);
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
