package models.llvm.caculate;

import models.llvm.IR;
import models.llvm.IrList;

public class ZextIR implements IR {
    private final String result;
    private final String value;

    public ZextIR(String value) {
        this.result = IrList.newReg();
        this.value = value;
    }

    @Override
    public String toString() {
        return "\t" + result + " = zext i1 " + value + " to i32\n";
    }
}
