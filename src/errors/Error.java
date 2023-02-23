package errors;

import io.WriteFile;

public class Error {
    public final String type;
    public final int line;

    public Error(String type, int line) {
        this.type = type;
        this.line = line;
    }

    @Override
    public String toString() {
        return line + " " + type + "\n";
    }
}
