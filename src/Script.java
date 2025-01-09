import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Script {
    String sourceCode;
    OS os;
    int i = 0;
    Script(byte[] srcBytes, OS os) {
        this.sourceCode = new String(srcBytes);
        this.os = os;
    }

    public void run() {
        // while (this.i < this.sourceCode.length()) {
        //     System.out.println(getFunc());
        // }
        UFrame window = new UFrame(300, 300);
        os.windows.add(window);

    }

    private String getFunc() {
        char thisChar = this.sourceCode.charAt(this.i);
        StringBuilder funcName = new StringBuilder();
        while (thisChar != ';') {
            if (this.i == this.sourceCode.length()) {
                break;
            }
            thisChar = this.sourceCode.charAt(this.i);
            if (thisChar != ';') {
                funcName.append(thisChar);
                this.i ++;
            }
        }

        return funcName.toString();
    }

    public String toString() {
        return "Executable: '" + this.sourceCode + "'";
    }
}

class UFrame {
    int height;
    int width;
    int screenX;
    int screenY;
    boolean visible = true;
    int speed;
    byte[] pixels;
    BufferedImage image;
    UFrame(int height, int width) {
        this.height = height;
        this.width = width;
        this.screenX = new Random().nextInt(1000);
        this.screenY = new Random().nextInt(700);
        this.pixels = new byte[height * width * 3];
        this.speed = new Random().nextInt(10);
        for (int i = 0; i < this.pixels.length; i += 3) {
            pixels[i] = (byte) (i * (0.35 / width));
            pixels[i + 1] = (byte) 0;
            pixels[i + 2] = (byte) 255;
        }
        image = Utils.colorArrayToImage(pixels, width, height);
        /* for (int i = 0; i < height; i++) {
            for (int j = 0; j < height; j++) {
                pixels[i][j] = new Color((int) (j * 0.35), (int) (i * 0.35), 128);
            }
        } */
        new Thread(() -> new Timer(0, e -> {
            System.out.println("hi");
            for (int i = 0; i < height; i++) {
                pixels[i] = pixels[(i + speed * 3 * width) % height];
                image = Utils.colorArrayToImage(pixels, width, height);
            }
        }).start()).start();
    }
}