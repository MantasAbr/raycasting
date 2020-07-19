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
    
    public Sounds(String source){
        this.source = source;
    }
    
    public static Sounds stoneWalk = new Sounds("src/sounds/walking_stone.wav");
    public static Sounds stoneRun = new Sounds("src/sounds/running_stone.wav");
    
    public void PlaySelectedSound(String type){
        switch(type){
            case "walkStone":
                PlaySound(stoneWalk.source);
                break;
            case "runStone":
                PlaySound(stoneRun.source);
                break;
            default:
                
        }
    }
    
    public void PlaySound(String path){
        try{
            File sound = new File(path);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(sound);
            clip = AudioSystem.getClip();
            
            if(sound.exists()){                
                clip.open(audioInput);
                clip.start();
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
