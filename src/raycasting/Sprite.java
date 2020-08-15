package raycasting;

/**
 *
 * @author Mantas Abramavičius
 */
public class Sprite extends Texture{

    private double xLoc;
    private double yLoc;

    /**
     * The constructor will initialize the loc and SIZE variables and call
     * the method to load the image data into pixels
     *
     * @param location
     * @param size
     * @param name
     */
    public Sprite(double xLoc, double yLoc, String location, int size, String name) {
        super(location, size, name);
        this.xLoc = xLoc;
        this.yLoc = yLoc;
        super.load();
    }

    public double getXLoc(){
        return xLoc;
    }

    public double getYLoc(){
        return yLoc;
    }

    public static Sprite test = new Sprite(7.5, 7.5, "src/textures/wood.png", 64, "test");
}
