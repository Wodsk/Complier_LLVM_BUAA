package models.treemodels.root.exp.common;

import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.caculate.AndIR;
import models.llvm.caculate.MulIR;
import models.llvm.caculate.SDivIR;
import models.llvm.caculate.SremIR;
import models.treemodels.leaf.Token;
import models.treemodels.root.exp.UnaryExp;

import java.util.ArrayList;

public class MulExp extends Expression {
    private final ArrayList<UnaryExp> unaryExps = new ArrayList<>();
    public MulExp(UnaryExp unaryExp, int line) {
        code = NCode.MulExp;
        Token multi = new Token("MULT", "*", -1);
        super.addOption(multi);
        this.unaryExps.add(unaryExp);
        this.line = line;
    }

    public void addUnaryExp(UnaryExp unaryExp, Token option) {
        super.addOption(option);
        this.unaryExps.add(unaryExp);
    }

    public ArrayList<UnaryExp> getUnaryExps() {
        return unaryExps;
    }

    @Override
    public void translate() {
        unaryExps.get(0).translate();
        String op1 = IrList.getLastOp();
        String op2;
        for (int i = 1; i < unaryExps.size(); i++) {
            unaryExps.get(i).translate();
            op2 = IrList.getLastOp();
            int result;
            switch (getOption(i)) {
                case MULT:
                    if (isConstExp(op1, op2)) {
                       result = Integer.parseInt(op1) *  Integer.parseInt(op2);
                       IrList.setLastOp(String.valueOf(result));
                    }
                    else {
                        MulIR mulIR = new MulIR(op1, op2);
                        IrList.getInstance().addIr(mulIR);
                    }
                    break;
                case DIV:
                    if (isConstExp(op1, op2)) {
                        result = Integer.parseInt(op1) / Integer.parseInt(op2);
                        IrList.setLastOp(String.valueOf(result));
                    }
                    else {
                        SDivIR sDivIR = new SDivIR(op1, op2);
                        IrList.getInstance().addIr(sDivIR);
                    }
                    break;
                case MOD:
                    if (isConstExp(op1, op2)) {
                        result = Integer.parseInt(op1) % Integer.parseInt(op2);
                        IrList.setLastOp(String.valueOf(result));
                    }
                    else {
                        SremIR sremIR = new SremIR(op1, op2);
                        IrList.getInstance().addIr(sremIR);
                    }
                    break;
                case BITAND:
                    if (isConstExp(op1, op2)) {
                        result = Integer.parseInt(op1) & Integer.parseInt(op2);
                        IrList.setLastOp(String.valueOf(result));
                    }
                    else {
                        AndIR andIR = new AndIR(op1, op2);
                        IrList.getInstance().addIr(andIR);
                    }
                default:
                    break;
                    //exception here
            }
            op1 = IrList.getLastOp();
        }
    }

    @Override
    public int getDimension() {
        return unaryExps.get(0).getDimension();
    }
}
