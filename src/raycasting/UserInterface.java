package raycasting;

import java.awt.*;

public class UserInterface {

    Player player;
    Point SprintBarPoint = new Point(120, Raycasting.WINDOW_HEIGHT - 50);
    Point HealthBarPoint = new Point(120, Raycasting.WINDOW_HEIGHT - 100);
    private int barHeight = 20;

    Font font = new Font("Courier new", Font.BOLD ,16);

    public UserInterface(Player player){
        this.player = player;
    }

    public void DrawInterface(Graphics g){
        g.setFont(font);
        drawSprintInfo(g, player);
        drawHealthInfo(g, player);
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
}
