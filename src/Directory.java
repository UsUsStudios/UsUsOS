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

    public Object getFromName(String name) {
        for (Object object : dir) {
            if (object instanceof Directory directory) {
                if (directory.dirName.equals(name)) {
                    return directory;
                }
            } else if (object instanceof ComFile comfile) {
                if ((comfile.fileName + "." + comfile.fileExtension).equals(name)) {
                    return comfile;
                }
            }
        }

        throw new RuntimeException("Directory/File '" + name + "' is not available");
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

    public Directory getDirFromPath(String path) {
        String[] parts = path.split("/");
        Object lastDirObject = this;
        for (String part : parts) {
            lastDirObject = ((Directory) lastDirObject).getFromName(part);
        }

        return (Directory) lastDirObject;
    }

    public ComFile getFileFromPath(String path) {
        String[] parts = path.split("/");
        Object lastDirObject = this;
        for (String part : parts) {
            lastDirObject = ((Directory) lastDirObject).getFromName(part);
        }

        return (ComFile) lastDirObject;
    }
}
