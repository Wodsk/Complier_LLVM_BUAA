package models.treemodels.root;

import generator.Translation;
import io.WriteFile;
import models.encode.NCode;
import symbol.SymbolTable;

import java.io.IOException;

public class Root implements Translation {
    private SymbolTable selfTable;

    protected int line;

    protected NCode code;

    public NCode getCode() {
        return code;
    }
    public void printRoot() throws IOException {
        //WriteFile writeFile = WriteFile.getInstance();
        //writeFile.outputString(code.toString());
    }

    public void setSelfTable(SymbolTable selfTable) {
        this.selfTable = selfTable;
    }

    public SymbolTable getSelfTable() {
        return selfTable;
    }

    @Override
    public void translate() {
    }

    public int getLine() {
        return line;
    }

}
