package models.treemodels.root.exp.common;

import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.caculate.AddIR;
import models.llvm.caculate.SubIR;
import models.treemodels.leaf.Token;

import java.util.ArrayList;

public class AddExp extends Expression {
    private final ArrayList<MulExp> mulExps = new ArrayList<>();

    public AddExp(MulExp mulExp, int line) {
        code = NCode.AddExp;
        this.mulExps.add(mulExp);
        Token add = new Token("PLUS", "+", -1);
        super.addOption(add);
        this.line = line;
    }

    public void addMulExp(MulExp mulExp, Token option) {
        super.addOption(option);
        this.mulExps.add(mulExp);
    }

    public ArrayList<MulExp> getMulExps() {
        return mulExps;
    }

    @Override
    public void translate() {
        mulExps.get(0).translate();
        String op1 = IrList.getLastOp();
        String op2;
        for (int i = 1; i < mulExps.size(); i++) {
            mulExps.get(i).translate();
            op2 = IrList.getLastOp();
            int result;
            switch (getOption(i)) {
                case PLUS:
                    if (isConstExp(op1, op2)) {
                        result = Integer.parseInt(op1) + Integer.parseInt(op2);
                        IrList.setLastOp(String.valueOf(result));
                    } else {
                        AddIR addIR = new AddIR(op1, op2);
                        IrList.getInstance().addIr(addIR);
                    }
                    break;
                case MINU:
                    if (isConstExp(op1, op2)) {
                        result = Integer.parseInt(op1) - Integer.parseInt(op2);
                        IrList.setLastOp(String.valueOf(result));
                    } else {
                        SubIR subIR = new SubIR(op1, op2);
                        IrList.getInstance().addIr(subIR);
                    }
                    break;
                default:
                    break;
                //exception here
            }
            op1 = IrList.getLastOp();
        }
    }

    @Override
    public int getDimension() {
        return mulExps.get(0).getDimension();
    }
}
