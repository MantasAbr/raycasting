package launcher;

import raycasting.Raycasting;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Launcher extends JFrame {

    private JPanel window = new JPanel();
    private JButton play, options, about, exit;
    private Rectangle rectPlay, rectOptions, rectAbout, rectExit;

    private final int width = 280;
    private final int height = 310;
    private final int button_width = 100;
    private final int button_height = 40;

    public Launcher(){
        try{
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception e){
            e.printStackTrace();
        }
        setTitle("Puzzled Launcher");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().add(window);
        setLocationRelativeTo(null);
        setResizable(false);
        setResizable(true);
        setVisible(true);
        window.setLayout(null);

        renderButtons();
    }

    private void renderButtons(){
        play = renderButton(play, rectPlay, "Play", 40);
        options = renderButton(options, rectOptions, "Options", 90);
        about = renderButton(about, rectAbout, "About", 140);
        exit = renderButton(exit, rectExit, "Exit", 190);

        play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Raycasting();
            }
        });

        options.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Raycasting();
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    private JButton renderButton(JButton jButton, Rectangle rButton, String text, int y){
        jButton = new JButton(text);
        rButton = new Rectangle((width / 2) - (button_width / 2), y, button_width, button_height);
        jButton.setBounds(rButton);
        window.add(jButton);
        return jButton;
    }
}
