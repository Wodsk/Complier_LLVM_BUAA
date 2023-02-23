package models.llvm;

public enum Instruction {
    add, sub, mul, sdiv,
    icmp, and, or, call,
    alloca, load, store,
    getelementptr, br, ret, srem
}
