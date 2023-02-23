package models.llvm.control;

import models.llvm.IR;
import models.llvm.IrList;

public class IcmpIR implements IR {
    private final String cond;
    private final String op1;
    private final String op2;
    private final String result;

    public IcmpIR(String cond, String op1, String op2) {
        this.cond = cond;
        this.op1 = op1;
        this.op2 = op2;
        result = IrList.newReg();
    }

    @Override
    public String toString() {
        return "\t" + result + " = icmp " + cond + " i32 " + op1 + ", " + op2 + "\n";
    }
}
