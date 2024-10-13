import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Computer {
    // Sets up the frame and boots the OS
    public static void boot() throws IOException, ClassNotFoundException {
        JFrame frame = new JFrame("UsUsOS");
        frame.setVisible(true);
        frame.setSize(830, 830);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        OS os = new OS(830, 830, frame);
        frame.pack();

        os.requestFocus();
        os.startUp();
    }

    // Sends a request to version.usus and to version.usus on GitHub and compares them
    public static boolean checkVersionNumber() throws FileNotFoundException {
        String versionNumber;
        try (Scanner reader = new Scanner(new File(Utils.getPath("version.usus")))) {
            versionNumber = reader.next() + "\n";
        }

       String cloudVersionNumber = VersionFetcher.getVersionNumber();

       return (versionNumber.equals(cloudVersionNumber));
    }

    // Serializes and writes the inputted Directory to storage.usus
    public static void saveDir(Directory mainDir) throws IOException {
        // Serialize
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(mainDir);
        oos.flush();
        
        // Write to file
        try (FileWriter writer = new FileWriter(Utils.getPath("storage.usus"))) {
            byte[] byteArray = bos.toByteArray();
            writer.write(Base64.getEncoder().encodeToString(byteArray));
        }
    }

    // Reads and deserializes storage.usus into a Directory instance
    public static Directory loadDir() throws IOException, ClassNotFoundException {
        // Read File
        File file = new File(Utils.getPath("storage.usus"));
        String storageString;
        try (Scanner reader = new Scanner(file)) {
            storageString = "";
            while (reader.hasNextLine()) {
                storageString += reader.next();
            }
        }

        // Deserialize
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(storageString);
            ByteArrayInputStream newBos = new ByteArrayInputStream(decodedBytes);
            ObjectInputStream ois = new ObjectInputStream(newBos);            
            return (Directory) ois.readObject();
        } catch (EOFException e) {
            Directory mainDir = new Directory("C:");
            saveDir(mainDir);
            return mainDir;
        }
    }
}

class OS extends JPanel implements ActionListener, KeyListener {
    String text;
    String submittedText;
    int width;
    int height;
    boolean isAccepting;
    boolean isLatestVersion;
    Directory mainDir;
    int mode;
    ArrayList<Script> scripts;

    // Sets up the OS
    OS(int width, int height, JFrame frame) throws IOException, ClassNotFoundException {
        frame.add(this);
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(this.width, this.height));
        setBackground(Color.BLACK);

        this.text = "Starting boot process";
        this.submittedText = "";
        this.isAccepting = false;
        this.mode = 0;
        this.scripts = new ArrayList<>();
        
        new Timer(100, (ActionEvent e) -> {
            repaint();
        }).start();

        this.text = "Checking version";
        this.isLatestVersion = Computer.checkVersionNumber();
        this.text = "Loading storage";
        this.mainDir = Computer.loadDir();
        this.text = "";

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (isAccepting) {
                    submittedText += e.getKeyChar();
                    processKey(e.getKeyCode());
                }
            }
        });
    }

    // Checks the storage, version and does the command prompt sign-in/sign up
    public void startUp() throws IOException, ClassNotFoundException {
        if (this.isLatestVersion) {
            new Thread(() -> {
                if (this.mainDir.isNullDir() || this.mainDir.isEmpty()) {
                    // Sign up
                    try {
                        echo("Welcome to UsUsOS! Let's get you set up.");
                        Map<String, String> userData = new HashMap<>();
                        userData.put("Username", getUserInput("\nEnter your username: "));
                        userData.put("Password", getUserInput("\nEnter your password: "));
                        echo("\nSetting up your storage. Please wait a moment.");
                        byte[] userDataBytes = Utils.mapToByteArray(userData);
                        setupStorage(userDataBytes);
                        getUserInput("\nStorage complete! Press enter to continue to home screen.");
                        this.mode = 1;
                    } catch (IOException ex) {
                        echo("\nAn error occurred. Please try again. If the error persists, please get help from the creator by creating an issue on the GitHub page.\nError: " + ex.getMessage() + Arrays.toString(ex.getStackTrace()));
                    }
                } else {
                    try {
                        // Sign-in
                        HashMap<String, String> userData = Utils.byteArrayToMap(this.mainDir.getFileFromPath("UsUsOS/userdata.sys").data);
                        echo("Hello, " + userData.get("Username") + ".");
                        String password = getUserInput("\nPlease enter your password: ");
                        while (!userData.get("Password").equals(password)) {
                            password = getUserInput("\nIncorrect password. Please enter your password: ");
                        }
                        echo("\nYou have successfully signed in. Entering desktop mode...");
                        this.mode = 1;
                        runScript("UsUsOS/testExe.exe");
                    } catch (IOException | ClassNotFoundException e) {
                        echo("\nAn error occurred. Please try again. If the error persists, please get help from the creator by creating an issue on the GitHub page.\nError: " + e.toString() + Arrays.toString(e.getStackTrace()));
                    }
                }
            }).start();
        } else {
            new Thread(() -> {
                // New version available
                echo("There is a new version of UsUsOS available. Please download at https://github.com/UsUsStudios/UsUsOS.");
                if (Desktop.isDesktopSupported()) {
                    try {
                        URI uri = new URI("https://github.com/UsUsStudios/UsUsOS");
                        
                        Desktop desktop = Desktop.getDesktop();
                        desktop.browse(uri);
                    } catch (IOException | URISyntaxException e) {
                        System.err.println(e.getMessage());
                    }
                } else {
                    echo("\nDesktop not supported.");
                }
            }).start();    
        }   
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Checks mode (command prompt/desktop)
    public void draw(Graphics g) {
        switch (this.mode) {
            case 0 -> drawCommandPrompt(g);
            case 1 -> drawDesktop(g);
        }
    }

    // Processes key presses, other than writing the letter to the command prompt
    private void processKey(int keyCode) {
        if (keyCode == KeyEvent.VK_BACK_SPACE) {
            try {
                this.submittedText = this.submittedText.substring(0, this.submittedText.length() - 2);
            } catch (StringIndexOutOfBoundsException _) {}
        }
    }

    // Draws the command prompt (all just text)
    private void drawCommandPrompt(Graphics g) {
        g.setColor(Color.WHITE);
        Font font = new Font("Monospaced", Font.BOLD, 16);
        g.setFont(font);
        int col = 0;
        int ln = 1;
        String textToPrint = this.text + this.submittedText;
        for (int i = 0; i < textToPrint.length(); i++) {
            char ch = textToPrint.charAt(i);
            if (col * 10 + 5 > this.width - 10) {
                col = 0;
                ln++;
            }
            if (isPrintable(ch)) {
                g.drawString(String.valueOf(ch), col * 10 + 5, ln * 20);
                col++;
            } else {
                if (ch == '\n') {
                    if (i == textToPrint.length() - 1) {
                        this.isAccepting = false;
                    } else {
                        col = 0;
                        ln++;
                    }
                }
            }
        }
    }

    // Draws the desktop (background image, will add windows too)
    private void drawDesktop(Graphics g) {
        try {
            File backgroundImg = new File(Utils.getPath("background.png"));
            Image img = ImageIO.read(backgroundImg).getScaledInstance(1200, 830, Image.SCALE_DEFAULT);
            g.drawImage(img, -300, 0, null);
        } catch (IOException _) {}
    }

    // Sets up the storage (for now saves the user data)
    private void setupStorage(byte[] userData) throws IOException {
        Directory OSDir = new Directory("UsUsOS");
        OSDir.add(new ComFile("userdata", "sys", userData));

        File file = new File(Utils.getPath("testExe.txt"));
        String testExeString;
        try (Scanner reader = new Scanner(file)) {
            testExeString = "";
            while (reader.hasNextLine()) {
                testExeString += reader.nextLine() + "\n";
            }
        }

        OSDir.add(new ComFile("testExe", "exe", testExeString.getBytes()));

        this.mainDir.add(OSDir);
        Computer.saveDir(this.mainDir);
    }

    // Adds a script to the script list and runs it
    private void runScript(String path) {
        byte[] scriptData = this.mainDir.getFileFromPath(path).data;
        Script script = new Script(scriptData);
        this.scripts.add(script);
        script.run();
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    // Checks if a character is printable (for the command prompt)
    public static boolean isPrintable(char ch) {
        return !Character.isISOControl(ch) && ch >= 32 && ch < 127;
    }

    // Waits for the user to press enter, and returns what the user's input was
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
        this.text = this.text + this.submittedText;
        this.text = this.text.substring(0, this.text.length() - 1);
        this.submittedText = "";

        return input;
    }

    public void echo(String text) {
        this.text += text;
    }
}
