package models.llvm.control;

import models.llvm.IR;

public class BrIR implements IR {
    private String cond = null;
    private String ifTrue = null;
    private String ifFalse = null;

    private String dest = null;

    public BrIR(String cond, String ifTrue, String ifFalse) {
        this.cond = cond;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    public BrIR(String string, int type) {
        if (type == 1) {
            this .dest = string;
        }
        else {
            this.cond = string;
        }
    }

    public BrIR(){}

    public void setDest(String dest) {
        this.dest = dest;
    }

    public void setIfTrue(String ifTrue) {
        this.ifTrue = ifTrue;
    }

    public void setIfFalse(String ifFalse) {
        this.ifFalse = ifFalse;
    }

    @Override
    public String toString() {
        if (dest != null) {
            return "\tbr label %" + dest + "\n";
        }
        else {
            return "\tbr i1 " + cond + ", label %" + ifTrue + ", label %" + ifFalse + "\n";
        }
    }
}
