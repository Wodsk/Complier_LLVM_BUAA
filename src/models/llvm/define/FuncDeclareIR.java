package models.llvm.define;

import models.llvm.IR;

import java.util.ArrayList;

public class FuncDeclareIR implements IR {
    private final boolean isVoid;
    private final String name;
    private final ArrayList<Integer> levels;

    public FuncDeclareIR(boolean isVoid, String name, ArrayList<Integer> levels) {
        this.isVoid = isVoid;
        this.name = name;
        this.levels = levels;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("declare ");
        if (isVoid)
            s.append("void @");
        else
            s.append("i32 @");
        s.append(name).append("(");
        for (int i = 0; i < levels.size(); i++) {
            int integer = levels.get(i);
            switch (integer) {
                case 0:
                    s.append("i32");
                    break;
                case 1:
                    s.append("i32*");
                default:
                    break;
            }
            if (i != levels.size() - 1) {
                s.append(", ");
            }
        }
        s.append(")\n");
        return s.toString();
    }
}
