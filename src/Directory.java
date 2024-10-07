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

    @Override
    public String toString() {
        return this.dirName + ": " + this.dir;
    }
}
