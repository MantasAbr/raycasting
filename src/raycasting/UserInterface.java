package raycasting;

import gui.GUIElement;
import gui.StyleColors;
import input.Input;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class UserInterface {

    private Player player;
    private Input input;
    private ArrayList<GUIElement> gui;

    private Point SprintBarPoint = new Point(120, Raycasting.WINDOW_HEIGHT - 50);
    private Point HealthBarPoint = new Point(120, Raycasting.WINDOW_HEIGHT - 100);
    private int barHeight = 20;

    private Font font = new Font("Yoster Island", Font.BOLD ,20);
    private Font pauseSplashText = new Font("Yoster Island", Font.BOLD, 80);

    private Rectangle exitButton;
    private Rectangle saveGameButton;
    private Rectangle loadGameButton;

    public UserInterface(Player player, Input input, ArrayList<GUIElement> gui){
        this.player = player;
        this.input = input;
        this.gui = gui;
    }

    public void DrawInterface(Graphics g){
        g.setFont(font);
        drawSprintInfo(g, player);
        drawHealthInfo(g, player);
    }

    public void drawOptions(Graphics g){
        drawOptionsScreen(g, player);
    }

    public void drawPauseSplashText(Graphics g){
        g.setColor(StyleColors.white.getColor());
        drawCenteredString(g, "Paused", 0, 0, Raycasting.WINDOW_WIDTH, Raycasting.WINDOW_HEIGHT, pauseSplashText);
    }

    private void drawHealthInfo(Graphics g, Player player){
        g.setColor(Color.WHITE);
        g.drawString("Helf", HealthBarPoint.x - 90, (int)(HealthBarPoint.y + barHeight / 1.4));
        g.setColor(Color.RED);
        g.fillRect(HealthBarPoint.x, HealthBarPoint.y, (int)(player.getHealthValue() * 1.5), barHeight);
    }

    private void drawSprintInfo(Graphics g, Player player){
        g.setColor(Color.WHITE);
        g.drawString("Spirtn", SprintBarPoint.x - 90, (int)(SprintBarPoint.y + barHeight / 1.4));
        g.setColor(Color.BLUE);
        g.fillRect(SprintBarPoint.x, SprintBarPoint.y, (int)(player.getSprintValue() * 1.5), barHeight);
    }

    private void drawOptionsScreen(Graphics g, Player player){
        g.drawImage(gui.get(0).getElementImage(), 250, 100, null);
        saveGameButton = drawButton(g, 320, 160, 40, "Save game", font, StyleColors.white.getColor());
        loadGameButton = drawButton(g, 320, 235, 40, "Load game", font, StyleColors.white.getColor());
        exitButton = drawButton(g, 325, 400, 40, "Exit game", font, StyleColors.darkred.getColor());
    }

    private Rectangle drawButton(Graphics g, int x, int y, int height,
                                 String text, Font font, Color fontColor){
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(text, g);
        int offset = 30;

        g.drawImage(gui.get(1).getElementImage().getScaledInstance((int) bounds.getWidth() + offset, height, 1), x, y, null);
        Rectangle button = new Rectangle(x, y, (int) bounds.getWidth() + offset, height);

        g.setColor(fontColor);
        g.setFont(font);
        int stringX = x + (int)(offset / 2);
        int stringY = y + (int)((height / 2) + (int)(bounds.getHeight() / 3));
        g.drawString(text, stringX, stringY);

        return button;
    }

    private void drawCenteredString(Graphics g, String text, int boundX, int boundY, int width, int height, Font font){
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = boundX + (width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = boundY + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Draw the String
        g.drawString(text, x, y);
    }

    public Rectangle getExitButton() {
        return exitButton;
    }
    public Rectangle getSaveGameButton() { return saveGameButton; }
    public Rectangle getLoadGameButton(){return loadGameButton;}
}
