package raycasting;

/**
 *
 * @author Mantas Abramavičius
 */
public class Sprite {
    
    double xPosition;
    double yPosition;
    
    double distanceToCamera;
    double cameraWallDistanceFactor;
    
    double xTransformed;
    double yTransformed;
    
    private Texture texture;
    
    public Sprite(double xPosition, double yPosition, Texture texture){
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.texture = texture;
    }
    
    public Texture getTexture() {
            return texture;
    }
}
