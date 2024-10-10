import java.io.Serializable;
import java.util.ArrayList;

public class Directory implements Serializable {
    String dirName;
    ArrayList<Object> dir;

    Directory(String dirName) {
        this.dirName = dirName;
        this.dir = new ArrayList<>();
    }

    public void add(Object object) {
        if (object instanceof Directory || object instanceof ComFile) {
            dir.add(object);
        }
    }

    public Directory getDir(String name) {
        for (Object object : dir) {
            if (object instanceof Directory directory) {
                if (directory.dirName.equals(name)) {
                    return directory;
                }
            }
        }

        return new Directory("null");
    }

    public ComFile getFile(String name) {
        for (Object object : dir) {
            if (object instanceof ComFile file) {
                if ((file.fileName + "." + file.fileExtension).equals(name)) {
                    return file;
                }
            }
        }

        return new ComFile("null", "null", new ArrayList<>());
    }

    @Override
    public String toString() {
        return "'" + this.dirName + "': " + this.dir;
    }

    public boolean isEmpty() {
        return this.dir.isEmpty();
    }

    public boolean isNullDir() {
        return this.dir.isEmpty() && this.dirName.equals("null");
    }
}
