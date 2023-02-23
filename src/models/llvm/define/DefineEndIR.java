package models.llvm.define;

import models.llvm.IR;

public class DefineEndIR implements IR {
    @Override
    public String toString() {
        return "}\n\n";
    }
}
