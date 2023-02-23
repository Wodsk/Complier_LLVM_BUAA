package errors;

import io.WriteFile;
import models.llvm.IR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ErrorList {
    private static final ErrorList ERROR_LIST = new ErrorList();
    private final ArrayList<Error> errors = new ArrayList<>();

    public static ErrorList getInstance() {
        return ERROR_LIST;
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public void print() throws IOException {
        Collections.sort(errors, new Comparator<Error>() {
            @Override
            public int compare(Error o1, Error o2) {
                return o1.line - o2.line;
            }
        });
        for (Error error : errors) {
            WriteFile writeFile = WriteFile.getInstance();
            writeFile.outputString(error.toString());
        }
    }
}
