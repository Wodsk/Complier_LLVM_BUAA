package models.treemodels.root.exp.common;

import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.caculate.ZextIR;
import models.llvm.control.IcmpIR;
import models.treemodels.leaf.Token;

import java.util.ArrayList;

public class EqExp extends Expression {
    private final ArrayList<RelExp> relExps = new ArrayList<>();

    public EqExp(RelExp relExp, int line) {
        code = NCode.EqExp;
        this.relExps.add(relExp);
        this.line = line;
    }

    public ArrayList<RelExp> getRelExps() {
        return relExps;
    }

    public void addRelExp(RelExp relExp, Token option) {
        super.addOption(option);
        this.relExps.add(relExp);
    }

    @Override
    public void translate() {
        relExps.get(0).translate();
        String op1 = IrList.getLastOp();
        String op2;
        for (int i = 1; i < relExps.size(); i++) {
            relExps.get(i).translate();
            op2 = IrList.getLastOp();
            switch (getOption(i - 1)) {
                case EQL:
                    IrList.getInstance().addIr(new IcmpIR("eq", op1, op2));
                    break;
                case NEQ:
                    IrList.getInstance().addIr(new IcmpIR("ne", op1, op2));
                    break;
                default:
                    break;
            }
            IrList.getInstance().addIr(new ZextIR(IrList.getLastOp()));
            op1 = IrList.getLastOp();
        }
    }
}
