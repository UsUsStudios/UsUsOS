import java.io.IOException;

public class SetRegistry {
    public static void main(String[] args) {
        try {
            // Associate the file extension with a file type
            ProcessBuilder processBuilder1 = new ProcessBuilder(
                "reg", "add", "HKEY_CLASSES_ROOT\\.usus", "/ve", "/t", "REG_SZ", "/d", "ususfile", "/f"
            );
            Process process1 = processBuilder1.start();
            process1.waitFor();

            // Set the description for the file type
            ProcessBuilder processBuilder2 = new ProcessBuilder(
                "reg", "add", "HKEY_CLASSES_ROOT\\ususfile", "/ve", "/t", "REG_SZ", "/d", "UsUsOS Data File", "/f"
            );
            Process process2 = processBuilder2.start();
            process2.waitFor();

            System.out.println("Registry updated successfully.");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
