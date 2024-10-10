import java.io.Serializable;
import java.util.ArrayList;

public class ComFile implements Serializable {
    String fileName;
    String fileExtension;
    ArrayList<Byte> data;

    ComFile(String fileName, String fileExtension, ArrayList<Byte> data) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.data = data;
    }

    @Override
    public String toString() {
        return this.fileName + "." + this.fileExtension;
    }

    public ArrayList<Byte> getData() {
        return data;
    }
}
