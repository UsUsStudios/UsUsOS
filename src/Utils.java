import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static byte[] mapToByteArray(Map<String, String> map) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(map);
        }
        return bos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, String> byteArrayToMap(byte[] byteArray) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
        HashMap<String, String> map;
        ObjectInputStream ois = new ObjectInputStream(bis);
        map = (HashMap<String, String>) ois.readObject();
        return map;
    }

    public static String getPath(String filename) {
        String location = Computer.class.getResource("Computer.class").toString();
        if (location.startsWith("jar:")) {
            return System.getProperty("user.dir") + "\\" + filename;
        }
        return System.getProperty("user.dir") + "\\ususos\\" + filename;
    }
}
