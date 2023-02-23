package models.llvm.memory;

import models.llvm.IR;
import models.llvm.Instruction;

public class AllocaIR implements IR {
    private final String reg;
    private String d1 = null;
    private String d2 = null;

    public AllocaIR(String reg) {
        this.reg = reg;
    }

    public AllocaIR(String reg, String d1) {
        this.reg = reg;
        this.d1 = d1;
    }

    public AllocaIR(String reg, String d1, String d2) {
        this.reg = reg;
        this.d1 = d1;
        this.d2 = d2;
    }

    @Override
    public String toString() {
        if (d1 != null && d2 != null) {
            int d = Integer.parseInt(d1) * Integer.parseInt(d2);
            return "\t" + reg + " = alloca " + "[" + d +
                    " x i32] ;" + "\n";

        }
        else if (d1 != null) {
            return "\t" + reg + " = alloca " + "[" + d1 +
                    " x i32] ;" + "\n";
        }
        else {
            return "\t" + reg + " = " +
                    Instruction.alloca +
                    " i32" + "\n";
        }
    }
}
