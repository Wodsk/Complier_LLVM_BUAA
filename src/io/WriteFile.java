package io;

import models.treemodels.leaf.Token;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class WriteFile {
    private Writer writer;
    private static final WriteFile instance = new WriteFile();

    public static WriteFile getInstance() {
        return instance;
    }
    private WriteFile() {
        try {
            writer = new FileWriter("llvm_ir.txt");
            //writer = new FileWriter("llvm_ir_temp.txt");
            //writer = new FileWriter("error.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void outputToken(Token token)
            throws IOException {
        if (token.isValid()) {
            writer.write(token.getSymbol());
            writer.write(" ");
            writer.write(token.getValue());
            writer.write("\n");
        }
    }

    public void outputString(String string) throws IOException {
        writer.write(string);
        //writer.write("\n");
    }

    public void close() throws IOException {
        writer.close();
    }

}
