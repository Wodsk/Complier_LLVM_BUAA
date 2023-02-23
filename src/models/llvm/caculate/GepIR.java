package models.llvm.caculate;

import models.llvm.IR;
import models.llvm.IrList;

public class GepIR implements IR {
    private final  String base;
    private String offset;

    private int len = -1;
    private final String result;

    public GepIR(String base, String offset) {
        this.base = base;
        this.offset = offset;
        result = IrList.newReg();
    }

    public GepIR(String base, int offset) {
        this.base = base;
        this.offset = String.valueOf(offset);
        result = IrList.newReg();
    }

    public GepIR(int len, String base) {
        this.len = len;
        this.base = base;
        result = IrList.newReg();
    }

    @Override
    public String toString() {
        if (len == -1) {
            return "\t" + result + " = getelementptr i32, i32* " +
                    base + ", i32 " + offset + "\n";
        } else {
            return "\t" + result + " = getelementptr [" + len + " x i32]," +
                    " [" + len + " x i32]* " + base + ", i32 0, i32 0\n";
        }
    }
}

