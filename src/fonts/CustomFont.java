package fonts;

import raycasting.Raycasting;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class CustomFont {

    Raycasting game;
    String location;

    public CustomFont(Raycasting game, String location){
        this.game = game;
        this.location = location;
        createFont(game, location);
    }

    private void createFont(Raycasting game, String location){
        try {
            Font gameFont = Font.createFont(Font.TRUETYPE_FONT, new File(location)).deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(gameFont);
            game.setFont(gameFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }
}
