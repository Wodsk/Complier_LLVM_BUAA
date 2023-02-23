package models.treemodels.root.func;

import models.encode.NCode;
import models.llvm.IrList;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.exp.Exp;

import java.util.ArrayList;

public class FuncRParams extends Root {
    private final ArrayList<Exp> exps = new ArrayList<>();
    private final ArrayList<String> params = new ArrayList<>();

    private final ArrayList<Integer> paramLevels = new ArrayList<>();
    public FuncRParams(Exp exp, int line) {
        exps.add(exp);
        code = NCode.FuncRParams;
        this.line = line;
    }

    public void addExp(Exp exp) {
        this.exps.add(exp);
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public void translate() {
        for (Exp exp : exps) {
            exp.translate();
            params.add(IrList.getLastOp());
            //add which reg store the value of Variable
            paramLevels.add(exp.getDimension());
        }
        //different call has different params
    }

    public ArrayList<String> getParams() {
        return params;
    }

    public ArrayList<Integer> getParamLevels() {
        return paramLevels;
    }

}
