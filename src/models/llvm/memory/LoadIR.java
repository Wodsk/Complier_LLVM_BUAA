package models.llvm.memory;

import models.llvm.IR;
import models.llvm.Instruction;
import models.llvm.IrList;


public class LoadIR implements IR {
    private final String address;
    private final String resultReg;

    public LoadIR(String address) {
        this.address = address;
        resultReg = IrList.newReg();
    }

    @Override
    public String toString() {
        return  "\t" + resultReg +
                " = " + Instruction.load +
                " i32, i32* " + address +
                "\n";
    }
}
