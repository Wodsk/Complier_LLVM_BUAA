package models.llvm.caculate;

import models.llvm.IR;
import models.llvm.Instruction;
import models.llvm.IrList;

public class SremIR implements IR {
    private final String op1;
    private final String op2;

    private final String resultReg;

    public SremIR(String op1, String op2) {
        this.op1 = op1;
        this.op2 = op2;
        resultReg = IrList.newReg();
    }

    @Override
    public String toString() {
        return  "\t" + resultReg +
                " = " + Instruction.srem +
                " i32 " + op1 +
                ", " + op2 +
                "\n";
    }
}
