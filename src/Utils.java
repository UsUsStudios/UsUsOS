import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Utils {
    // Convert a map to a byte[]
    public static byte[] mapToByteArray(Map<String, String> map) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(map);
        }
        return bos.toByteArray();
    }

    // Convert a byte[] to a map
    @SuppressWarnings("unchecked")
    public static HashMap<String, String> byteArrayToMap(byte[] byteArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        HashMap<String, String> map;
        ObjectInputStream ois = new ObjectInputStream(bis);
        map = (HashMap<String, String>) ois.readObject();
        return map;
    }

    // Get the path of a file in the ususos directory (not aד easy as it sounds!)
    public static String getPath(String filename) {
        String location = Objects.requireNonNull(Computer.class.getResource("Computer.class")).toString();
        if (location.startsWith("jar:")) {
            return System.getProperty("user.dir") + "\\" + filename;
        }
        return System.getProperty("user.dir") + "\\ususos\\" + filename;
    }

    /*
    public static BufferedImage colorArrayToImage(Color[][] colorArray) {
        int width = colorArray.length;
        int height = colorArray[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                pixels[y * width + x] = colorArray[x][y].getRGB();
            }
        }

        return image;
    }
    */
    public static BufferedImage colorArrayToImage(byte[] colorArray, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        System.arraycopy(colorArray, 0, pixels, 0, colorArray.length);

        return image;
    }
}
