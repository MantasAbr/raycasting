package raycasting;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class UserInterface {

    private Player player;
    private Camera camera;

    private Point SprintBarPoint = new Point(120, Raycasting.WINDOW_HEIGHT - 50);
    private Point HealthBarPoint = new Point(120, Raycasting.WINDOW_HEIGHT - 100);
    private int barHeight = 20;

    private Font font = new Font("Courier new", Font.BOLD ,16);
    private Font header = new Font("Courier new", Font.BOLD, 22);
    private Font pauseSplashText = new Font("Courier new", Font.BOLD, 80);

    private Color optionsColor = new Color(102, 102, 102, 200);
    private int optionsBoxOffset = 50;
    private Rectangle optionsBox = new Rectangle(optionsBoxOffset, optionsBoxOffset,
                             Raycasting.WINDOW_WIDTH - (optionsBoxOffset * 2),
                            Raycasting.WINDOW_HEIGHT - (optionsBoxOffset * 2));

    public UserInterface(Player player, Camera camera){
        this.player = player;
        this.camera = camera;
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
        g.setColor(Color.WHITE);
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
        g.setColor(optionsColor);
        g.fillRect(optionsBox.x, optionsBox.y, optionsBox.width, optionsBox.height);
        g.setFont(header);
        drawButton(g, 200, 200, 40, Color.red, "Test", font, Color.white);
        drawButton(g, 200, 280, 40, Color.black, "Longer test!", font, Color.white);
        drawButton(g, 200, 360, 40, Color.orange, "Parametrai", font, Color.white);
    }

    private void drawButton(Graphics g, int x, int y, int height,
                            Color buttonColor, String text, Font font, Color fontColor){
        FontMetrics fm = g.getFontMetrics();
        Rectangle2D bounds = fm.getStringBounds(text, g);
        int offset = 30;

        g.setColor(buttonColor);
        g.fillRect(x, y, (int) bounds.getWidth() + offset, height);

        g.setColor(fontColor);
        g.setFont(font);
        int stringX = x + (int)(offset / 2);
        int stringY = y + (int)((height / 2) + (int)(bounds.getHeight() / 3));
        g.drawString(text, stringX, stringY);
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
}
