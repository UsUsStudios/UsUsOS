import java.util.Base64;

public class Script {
    String sourceCode;
    Script(byte[] srcBytes) {
        this.sourceCode = new String(srcBytes);
    }

    public void run() {}

    public String toString() {
        return "Executable: '" + this.sourceCode + "'";
    }
}
