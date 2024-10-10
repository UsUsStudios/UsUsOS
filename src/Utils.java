import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Utils {
    public static ArrayList<Byte> mapToByteArray(Map<String, String> map) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(map);
        }
        byte[] bytes = bos.toByteArray();
        ArrayList<Byte> byteArray = new ArrayList<>();
        for (byte b : bytes) {
            byteArray.add(b);
        }
        return byteArray;
    }

    @SuppressWarnings("unchecked")
    public static HashMap<String, String> byteArrayToMap(ArrayList<Byte> byteArray) throws IOException, ClassNotFoundException {
        byte[] bytes = new byte[byteArray.size()];
        for (int i = 0; i < byteArray.size(); i++) {
            bytes[i] = byteArray.get(i);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        HashMap<String, String> map;
        ObjectInputStream ois = new ObjectInputStream(bis);
        map = (HashMap<String, String>) ois.readObject();
        return map;
    }
}
