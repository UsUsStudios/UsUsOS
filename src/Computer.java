import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Computer {
    public static JFrame initScreen() throws IOException {
        JFrame frame = new JFrame("Hey");
        frame.setVisible(true);
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Screen screen = new Screen("src/Screenshot 2024-09-21 071548.png");
        frame.add(screen);

        screen.requestFocus();

        return frame;
    }
}

class Screen extends JPanel implements ActionListener, KeyListener {
    BufferedImage image;

    Screen(String imagePath) throws IOException {
        this.image = ImageIO.read(new File(imagePath));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
