package models.treemodels.root.exp;

import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.memory.LoadIR;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.exp.common.Expression;


public class PrimaryExp extends Root {
    private Exp exp = null;
    private LVal lVal = null;
    private Token number;

    public PrimaryExp(Exp exp, int line) {
        code = NCode.PrimaryExp;
        this.exp = exp;
        this.line = line;
    }

    public PrimaryExp(LVal lVal, int line) {
        code = NCode.PrimaryExp;
        this.lVal = lVal;
        this.line = line;
    }

    public PrimaryExp(Token number, int line) {
        code = NCode.PrimaryExp;
        this.number = number;
        this.line = line;
    }


    public Exp getExp() {
        return exp;
    }

    public LVal getLVal() {
        return lVal;
    }

    public Token getNumber() {
        return number;
    }

    @Override
    public void translate() {
        if (exp != null) {
            exp.translate();
        }
        else if (lVal != null) {
            lVal.translate();
            if (lVal.isNeedLoad()) {
                LoadIR loadIR = new LoadIR(IrList.getLastOp());
                IrList.getInstance().addIr(loadIR);
            }
        }
        else {
            IrList.setLastOp(number.getValue());
        }
    }

    public int getDimension() {
        if (exp != null) {
            return exp.getDimension();
        }
        else if (lVal != null) {
            return lVal.getDimension();
        }
        else {
            return 0;
        }
    }
}
