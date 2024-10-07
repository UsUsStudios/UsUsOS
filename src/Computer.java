import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Computer {
    public static JFrame initScreen() throws IOException {
        JFrame frame = new JFrame("UsUsOS");
        frame.setVisible(true);
        frame.setSize(830, 830);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Screen screen = new Screen(830, 830);
        frame.add(screen);

        screen.requestFocus();
        screen.startUp();

        return frame;
    }

    public static void initStorage() throws IOException, ClassNotFoundException, IOException {
        Directory mainDir = new Directory("C:");
        mainDir.add(new Directory("Hello"));
        mainDir.add(new ComFile("Hi", new ArrayList<>()));
        mainDir.add(new ComFile("Hey there dummies", new ArrayList<>()));

        saveDir(mainDir);

        Directory storageDecoded = loadDir();
        
        System.out.println(storageDecoded);
    }

    private static void saveDir(Directory mainDir) throws IOException {
        // Serialize
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(mainDir);
        oos.flush();
        
        // Write to file
        try (FileWriter writer = new FileWriter("storage.txt")) {
            byte[] byteArray = bos.toByteArray();
            writer.write(Base64.getEncoder().encodeToString(byteArray));
        }
    }

    private static Directory loadDir() throws IOException, ClassNotFoundException {
        // Read File
        File file = new File("storage.txt");
        String storageString;
        try (Scanner reader = new Scanner(file)) {
            storageString = "";
            while (reader.hasNextLine()) {
                storageString += reader.next();
            }
        }

        // Deserialize
        byte[] decodedBytes = Base64.getDecoder().decode(storageString);
        ByteArrayInputStream newBos = new ByteArrayInputStream(decodedBytes);
        ObjectInputStream ois = new ObjectInputStream(newBos);

        return (Directory) ois.readObject();
    }
}

class Screen extends JPanel implements ActionListener, KeyListener {
    String text;
    String submittedText;
    int width;
    int height;
    boolean isAccepting;

    Screen(int width, int height) throws IOException {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(this.width, this.height));
        setBackground(Color.BLACK);
        this.text = "";
        this.submittedText = "";
        this.isAccepting = false;
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isAccepting) {
                    text += e.getKeyChar();
                    submittedText += e.getKeyChar();
                }
            }
        });
        Timer timer = new Timer(0, (ActionEvent e) -> {
            repaint();
        });
        timer.start();
    }

    public void startUp() {
        new Thread(() -> {
            String username = getUserInput("Welcome to UsUsOS. Please enter your username. ");
            String password = getUserInput("\nPlease enter your password. ");
            
            this.text += "\nThank you for signing in. Unfortunately, this is all there is to your UsUsOS experience at the moment.";

            System.out.println("'" + username + "'");
            System.out.println("'" + password + "'");
        }).start();
        
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.setColor(Color.WHITE);
        Font font = new Font("Monospaced", 1, 16);
        g.setFont(font);
        int col = 0;
        int ln = 1;
        for (int i = 0; i < this.text.length(); i++) {
            char ch = this.text.charAt(i);
            if (col * 10 + 5 > this.width - 30) {
                col = 0;
                ln++;
            }
            if (isPrintable(ch)) {
                g.drawString(String.valueOf(ch), col * 10 + 5, ln * 20);
                col++;
            } else {
                switch (ch) {
                    case '\n' -> {
                        if (i == this.text.length() - 1) {
                            this.isAccepting = false;
                        } else {
                            col = 0;
                            ln++;
                        }
                    }
                    default -> {
                    }
                        
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public static boolean isPrintable(char ch) {
        return !Character.isISOControl(ch) && ch >= 32 && ch < 127;
    }

    public String getUserInput(String text) {
        this.text += text;
        this.isAccepting = true;

        while (this.isAccepting) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        String input = this.submittedText.replace("\n", "");
        this.text = this.text.substring(0, this.text.length() - 1);
        this.submittedText = "";

        return input;
    }
}
