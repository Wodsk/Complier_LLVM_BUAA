package models.llvm;

import io.WriteFile;
import models.llvm.control.BrIR;

import java.io.IOException;
import java.util.ArrayList;

public class BasicBlock {
    private String label;
    private final ArrayList<IR> irList = new ArrayList<>();
    private BrIR brIR;

    public BasicBlock() {
        brIR = null;
        label = null;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void addIr(IR ir) {
        irList.add(ir);
    }

    public void setBr(BrIR brIR) {
        this.brIR = brIR;
    }

    public BrIR getBr() {
        return brIR;
    }

    public void print() throws IOException {
        WriteFile writeFile = WriteFile.getInstance();
        if (!label.equals("block0")) {
            writeFile.outputString(label);
            writeFile.outputString(":\n");
        }
        for (IR ir : irList) {
            writeFile.outputString(ir.toString());
        }
        if (brIR != null) {
            writeFile.outputString(brIR.toString());
        }
        writeFile.outputString("\n\n");
    }

}
