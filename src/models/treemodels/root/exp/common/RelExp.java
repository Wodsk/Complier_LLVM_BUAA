package models.treemodels.root.exp.common;

import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.caculate.ZextIR;
import models.llvm.control.IcmpIR;
import models.treemodels.leaf.Token;

import java.util.ArrayList;

import static models.llvm.IrList.getLastOp;

public class RelExp extends Expression {
    private final ArrayList<AddExp> addExps = new ArrayList<>();

    public RelExp(AddExp addExp, int line) {
        code = NCode.RelExp;
        this.addExps.add(addExp);
        this.line = line;
    }


    public void addAddExp(AddExp addExp, Token option) {
        super.addOption(option);
        this.addExps.add(addExp);
    }

    public ArrayList<AddExp> getRelExps() {
        return addExps;
    }

    @Override
    public void translate() {
        addExps.get(0).translate();
        String op1 = getLastOp();
        String op2;
        for (int i = 1; i < addExps.size(); i++) {
            addExps.get(i).translate();
            op2 = getLastOp();
            switch (getOption(i - 1)) {
                case LSS:
                    IrList.getInstance().addIr(new IcmpIR("slt", op1, op2));
                    break;
                case LEQ:
                    IrList.getInstance().addIr(new IcmpIR("sle", op1, op2));
                    break;
                case GRE:
                    IrList.getInstance().addIr(new IcmpIR("slt", op2, op1));
                    break;
                case GEQ:
                    IrList.getInstance().addIr(new IcmpIR("sle", op2, op1));
                    break;
                default:
                    break;
            }
            IrList.getInstance().addIr(new ZextIR(getLastOp()));
            op1 = getLastOp();
        }
    }
}
