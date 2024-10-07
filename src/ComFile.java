import java.io.Serializable;
import java.util.ArrayList;

public class ComFile implements Serializable {
    String fileName;
    ArrayList<Byte> data;

    ComFile(String fileName, ArrayList<Byte> data) {
        this.fileName = fileName;
        this.data = data;
    }

    @Override
    public String toString() {
        return this.fileName;
    }

    public ArrayList<Byte> getData() {
        return data;
    }
}
