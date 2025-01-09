import java.io.Serializable;

public class ComFile implements Serializable {
    String fileName;
    String fileExtension;
    byte[] data;

    ComFile(String fileName, String fileExtension, byte[] data) {
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.data = data;
    }

    @Override
    public String toString() {
        return this.fileName + "." + this.fileExtension;
    }

    public byte[] getData() {
        return data;
    }
}
