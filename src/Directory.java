import java.io.Serializable;
import java.util.ArrayList;

public class Directory implements Serializable {
    String dirName;
    ArrayList<Object> dir;

    // Sets up directory
    Directory(String dirName) {
        this.dirName = dirName;
        this.dir = new ArrayList<>();
    }

    // Adds a Directory or ComFile to the directory
    public void add(Object object) {
        if (object instanceof Directory || object instanceof ComFile) {
            dir.add(object);
        }
    }

    // Gets an immediate child of the directory
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

    // Null dirs are... complicated
    public boolean isNullDir() {
        return this.dir.isEmpty() && this.dirName.equals("null");
    }

    // Gets a directory from a path (with the working directory being this)
    public Directory getDirFromPath(String path) {
        String[] parts = path.split("/");
        Object lastDirObject = this;
        for (String part : parts) {
            lastDirObject = ((Directory) lastDirObject).getFromName(part);
        }

        return (Directory) lastDirObject;
    }

    // Gets a file from a path (with the working directory being this)
    public ComFile getFileFromPath(String path) {
        String[] parts = path.split("/");
        Object lastDirObject = this;
        for (String part : parts) {
            lastDirObject = ((Directory) lastDirObject).getFromName(part);
        }

        return (ComFile) lastDirObject;
    }
}
