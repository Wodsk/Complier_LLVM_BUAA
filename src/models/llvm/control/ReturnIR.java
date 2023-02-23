package models.llvm.control;

import models.llvm.IR;
import models.llvm.Instruction;

public class ReturnIR implements IR {
    private final String reg;

    public ReturnIR(String reg) {
        this.reg = reg;
    }

    @Override
    public String toString() {
        if (reg == null) {
            return "\t" + Instruction.ret + " void\n";
        } else {
            return "\t" + Instruction.ret + " i32 " + reg + "\n";
        }
    }
}
