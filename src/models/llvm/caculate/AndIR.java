package models.llvm.caculate;

import models.llvm.IR;
import models.llvm.IrList;

public class AndIR implements IR {
    private final String op1;
    private final String op2;

    private final String resultReg;

    public AndIR(String op1, String op2) {
        this.op1 = op1;
        this.op2 = op2;
        resultReg = IrList.newReg();
    }

    @Override
    public String toString() {
        return  "\t" + resultReg +
                " = and" +
                " i32 " + op1 +
                ", " + op2 +
                "\n";
    }
}
