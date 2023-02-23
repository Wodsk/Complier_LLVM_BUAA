package models.llvm.memory;

import models.llvm.IR;
import models.llvm.Instruction;

public class StoreIR implements IR {
    private final String option;
    private final String address;

    public StoreIR(String option, String address) {
        this.option = option;
        this.address = address;
    }

    @Override
    public String toString() {
        return "\t" + Instruction.store +
               " i32 " + option +
               ", i32* " + address + "\n";
    }
}
