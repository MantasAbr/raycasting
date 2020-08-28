package raycasting;

import java.io.File;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 *
 * @author Mantas Abramavičius
 */
public class Sounds {
    
    public String source;
    public Clip clip;
    public int frameLength;
    
    public Sounds(String source){
        this.source = source;
    }
    
    public static Sounds stoneWalk = new Sounds("src/sounds/walking_stone.wav");
    public static Sounds stoneRun = new Sounds("src/sounds/running_stone.wav");
    public static Sounds doorOpen = new Sounds("src/sounds/door_open.wav");
    public static Sounds stoneSneak = new Sounds("src/sounds/sneak_stone.wav");
    public static Sounds stoneFall = new Sounds("src/sounds/fall_stone.wav");
    
    public void PlaySound(boolean looping){
        try{
            File sound = new File(source);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(sound);
            clip = AudioSystem.getClip();
            
            if(sound.exists()){
                clip.open(audioInput);
                frameLength = clip.getFrameLength();
                clip.start();
                if(looping)
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
            }
            else{
                System.out.println("Sound file doesn't exist!");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
