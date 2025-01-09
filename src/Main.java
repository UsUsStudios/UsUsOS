/*
 * This file is not only the starting point of this program,
 * but also the starting point of you, the reader's, insanity.
 * Good luck.
 * 
 * Yes my code is pretty bad lol
*/

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Computer.boot();
        // resetStorage();
    }

    public static void resetStorage() throws Exception {
        OS os = new OS(80, 90, new JFrame());
        Map<String, String> userData = new HashMap<>();
        userData.put("Username", "1");
        userData.put("Password", "1");
        os.setupStorage(Utils.mapToByteArray(userData));
        Computer.saveDir(os.mainDir);
    }
}
