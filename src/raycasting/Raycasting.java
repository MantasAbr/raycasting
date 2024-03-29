package raycasting;
import fonts.CustomFont;
import gui.CustomButton;
import gui.GUIElement;
import items.InventorySlot;
import items.ItemLinkedList;
import input.Input;
import launcher.Launcher;
import levels.Level;
import levels.LevelDoorMesh;
import sounds.Sounds;
import sprites.Entity;

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
    public static int IMAGE_WIDTH = 800;
    public static int IMAGE_HEIGHT = 450;
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;
    public static final double MOUSE_SENSITIVITY = 150.5;
    public static int CURRENT_LEVEL;
    public static int RENDER_DISTANCE = 5;
    public static double FIELD_OF_VIEW = -1;

    //Used for the run() method
    private Thread thread;
    private volatile boolean running;
    public static boolean gameIsInInventory = false;
    public static boolean gameIsInOptions = false;
    
    //Used for displaying the image
    private BufferedImage image;
    public int[] pixels;
    
    //ArrayList for objects
    public ArrayList<Texture> textures;
    public ArrayList<ArrayList<Entity>> allLevelEntities;
    public ArrayList<Sounds> sounds;
    public ArrayList<Level> levels;
    public ArrayList<LevelDoorMesh> doorMeshes;
    public ArrayList<GUIElement> gui;
    public ArrayList<CustomButton> buttons;
    
    //Objects declarations
    public Camera camera;
    public Screen screen;
    public ActionHandling actions;
    public EntityHandling entityActions;
    public UserInterface userInterface;
    public Player player;
    public Input input;
    public CustomFont fonts;
    public ItemLinkedList inventoryItems;
    
    //used for showing the ticks and frames each second on the screen
    private int finalTicks = 0;
    private int finalFrames = 0;

    private long tickSum = 0;
    
    public Raycasting(){
        thread = new Thread(this);
        image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();

        audioInit();
        mouseInit();
        input = new Input(this, sounds, Pointer.blankPointer);
        textureInit();
        spriteInit();
        levelsInit();
        guiInit();
        inventoryInit();
        addKeyListener(input);
        addMouseListener(input);
        addMouseMotionListener(input);
        addMouseWheelListener(input);
        fonts = new CustomFont(this, "src/fonts/yoster.ttf");

        player = new Player(100, 100, .8);
        screen = new Screen(levels.get(CURRENT_LEVEL).getMap(), doorMeshes.get(CURRENT_LEVEL).getMap(),
                            levels.get(CURRENT_LEVEL).getMapWidth(), levels.get(CURRENT_LEVEL).getMapHeight(),
                            textures, allLevelEntities, CURRENT_LEVEL, IMAGE_WIDTH, IMAGE_HEIGHT, RENDER_DISTANCE);

        camera = new Camera(levels.get(CURRENT_LEVEL).getPlayerLocX(), levels.get(CURRENT_LEVEL).getPlayerLocY(),
                            FIELD_OF_VIEW, 0, 0, .66, sounds, this, screen, input);

        actions = new ActionHandling(camera, screen, sounds, input,this);
        entityActions = new EntityHandling(camera, Entity.firstLevelEntities, player);
        userInterface = new UserInterface(player, gui, buttons, inventoryItems);
        jFrameInit();             
        start();
    }
    
    /**
     * Used to load all the textures
     */
    private void textureInit(){
        textures = new ArrayList<Texture>();// ID's to use in txt files
        textures.add(Texture.wood);         // ID 1
        textures.add(Texture.brick);        // ID 2
        textures.add(Texture.stone);        // ID 3
        textures.add(Texture.woodBricks);   // ID 4
        textures.add(Texture.door);         // ID 5
        textures.add(Texture.levelDoor);    // ID 6
    }

    private void spriteInit(){
        Entity.firstLevelEntities.add(Entity.ceilingLampGreen1);
        Entity.firstLevelEntities.add(Entity.ceilingLampGreen2);
        Entity.firstLevelEntities.add(Entity.ceilingLampGreen3);
        Entity.firstLevelEntities.add(Entity.ceilingLampGreen4);
        Entity.firstLevelEntities.add(Entity.ceilingLampGreen5);
        Entity.firstLevelEntities.add(Entity.ceilingLampGreen6);
        //Entity.firstLevelEntities.add(Entity.revolverAmmo);
        //Entity.firstLevelEntities.add(Entity.box);
        Entity.firstLevelEntities.add(Entity.joke);
        //Entity.firstLevelEntities.add(Entity.table);
        //Entity.secondLevelEntities.add(Entity.ceilingLampBlack);

        allLevelEntities = new ArrayList<>();
        allLevelEntities.add(Entity.firstLevelEntities);
        allLevelEntities.add(Entity.secondLevelEntities);
        allLevelEntities.add(Entity.thirdLevelEntities);
    }
    
    private void audioInit(){
        sounds = new ArrayList<Sounds>();
        sounds.add(Sounds.stoneWalk);
        sounds.add(Sounds.stoneRun);
        sounds.add(Sounds.doorOpen);
        sounds.add(Sounds.stoneSneak);
        sounds.add(Sounds.stoneFall);
    }

    private void levelsInit(){
        CURRENT_LEVEL = 0;
        levels = new ArrayList<Level>();
        levels.add(Level.firstLevel);
        levels.add(Level.secondLevel);
        levels.add(Level.thirdLevel);

        doorMeshes = new ArrayList<LevelDoorMesh>();
        doorMeshes.add(LevelDoorMesh.firstLevelMesh);
        doorMeshes.add(LevelDoorMesh.secondLevelMesh);
        doorMeshes.add(LevelDoorMesh.thirdLevelMesh);
    }

    private void guiInit(){
        gui = new ArrayList<GUIElement>();
        gui.add(GUIElement.optionsScreen);
        gui.add(GUIElement.button);
        gui.add(GUIElement.inventoryScreen);
        gui.add(GUIElement.barOverlay);
        gui.add(GUIElement.lighterInventorySlot);
        long sum = gui.stream()
                .mapToLong(GUIElement::getElapsedTime)
                .sum();
        System.out.println("Time it took to load GUIElement objects: " + sum);

        buttons = new ArrayList<CustomButton>();
        buttons.add(GUIElement.saveGameButton);
        buttons.add(GUIElement.loadGameButton);
        buttons.add(GUIElement.exitGameButton);
    }

    private void inventoryInit(){
        inventoryItems = new ItemLinkedList();
        inventoryItems.addNode(InventorySlot.first);
        inventoryItems.addNode(InventorySlot.second);
        inventoryItems.addNode(InventorySlot.third);
        inventoryItems.addNode(InventorySlot.fourth);
        inventoryItems.addNode(InventorySlot.fifth);

    }
    
    private void mouseInit(){
        getContentPane().setCursor(Pointer.blankPointer.getPointer());
    }
    
    private void jFrameInit(){
        setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        setResizable(false);
        setTitle("Puzzled v0.1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setIconImage(textures.get(1).getTexImage());
        setBackground(Color.black);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setVisible(true);
    }
    
    private synchronized void start() {
        running = true;
        thread.start();
    }
    
    public synchronized void stop(){
        running = false;
        System.exit(1);
    }
    
    /**
     * Buffer strategy is used so that the updates are smoother;
     * To actually draw the image to the screen, a graphics object is obtained
     * from the buffer strategy and used to draw our image
     */
    public void render() {
        BufferStrategy bs = getBufferStrategy();
        if(bs == null){
            createBufferStrategy(4);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        image.getScaledInstance(SCREEN_WIDTH, SCREEN_HEIGHT, java.awt.Image.SCALE_SMOOTH);
        g.drawImage(image, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);


        if(Input.debug.isPressed()){
            debugInfo(g);
        }
        if(actions.levelChange){
            drawLoadScreen(g);
        }

        if(gameIsInOptions || gameIsInInventory){
            getContentPane().setCursor(Pointer.gamePointer.getPointer());
            if(gameIsInOptions){
                userInterface.drawOptionsScreen(g);
                getContentPane().setCursor(Pointer.gamePointer.getPointer());
                userInterface.drawInterface(g, false);
            }
            if(gameIsInInventory){
                userInterface.drawInventoryScreen(g);
                getContentPane().setCursor(Pointer.gamePointer.getPointer());
                userInterface.drawInterface(g, false);
            }
        }
        else{
            getContentPane().setCursor(Pointer.blankPointer.getPointer());
        }

        userInterface.drawInterface(g, true);

        g.dispose();

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
        g.drawString("Current level: " + CURRENT_LEVEL, 10, 150);
        g.drawString("Pitch: " + screen.pitch + ", posZ: " + screen.posZ, 10, 170);
        g.drawString("Currently holding: " + inventoryItems.getCurrentItem().getItem().getName(), 10, 190);
        g.drawString("Domio atstumas: " + Entity.joke.getDistance(), 10, 210);
    }

    public void drawLoadScreen(Graphics g){
        g.setColor(Color.black);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
    }
    
    public void tick(){
        if(!(gameIsInInventory || gameIsInOptions)){
            input.resetMousePointerForPlay();
            actions.resetPlayerFacingPosition();
            actions.GetNextBlock();
            actions.CheckForActions();
            actions.ApplyBlockChanges(levels.get(CURRENT_LEVEL).getMap());
            actions.ChangeLevel(levels, doorMeshes);
            actions.HandleButtonCombos();
            input.mouseMovementHandling(MOUSE_SENSITIVITY);
            input.mouseWheelHandling(userInterface, inventoryItems);
            entityActions.moveEntities();
            entityActions.applyHostileEntityDamage();
            player.ApplyUpdates(input);
        }
        if(gameIsInOptions){
            input.setMousePointerFlag();
            actions.setPlayerFacingPosition();
            input.optionsScreenClickHandling(userInterface);
            input.optionsScreenHoverHandling(userInterface);
        }
        if(gameIsInInventory){
            input.setMousePointerFlag();
            actions.setPlayerFacingPosition();
            input.inventoryScreenHoverHandling(inventoryItems);
            input.inventoryMovementHandling(inventoryItems);
        }
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
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / NanosPerTick;
            lastTime = now;
            boolean shouldRender = true; //if false, frame rate locks to the tick count, if not - vice versa
                
            while(delta >= 1){
                ticks++;
                tickSum++;
                tick();
                delta -= 1;
                shouldRender = true;
                screen.updateFloorAndCeiling(camera, pixels);
                screen.updateWalls(camera, pixels);
                screen.updateSprites(camera, pixels, CURRENT_LEVEL);
                camera.update(levels.get(CURRENT_LEVEL).getMap());
                if(input.exit.isPressed())
                    stop();
            }
                                
            try {
                //can increase the sleep rate to lower the fps and processing power
                Thread.sleep(7);
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

    public static void main(String [] args)
    {
        //Launcher launcher = new Launcher();
        new Raycasting();
    }
}
