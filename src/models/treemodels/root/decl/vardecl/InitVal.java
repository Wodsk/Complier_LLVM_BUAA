package models.treemodels.root.decl.vardecl;

import models.encode.NCode;
import models.llvm.IrList;
import models.treemodels.root.Root;
import models.treemodels.root.decl.constdecl.ConstInitVal;
import models.treemodels.root.exp.Exp;

import java.util.ArrayList;

public class InitVal extends Root {
    private Exp exp = null;
    private final ArrayList<InitVal> initVales = new ArrayList<>();

    public InitVal(int line) {
        code = NCode.InitVal;
        this.line = line;
    }

    public InitVal(Exp exp, int line) {
        this.exp = exp;
        code = NCode.InitVal;
        this.line = line;
    }

    public void addInitVal(InitVal initVal) {
        initVales.add(initVal);
    }

    public ArrayList<InitVal> getInitVales() {
        return initVales;
    }

    public Exp getExp() {
        return exp;
    }

    @Override
    public void translate() {
        //only variable value here
        if (exp != null) {
            exp.translate();
        }
    }

    public ArrayList<String> parserInitVal(int dimension) {
        ArrayList<String> initVales = new ArrayList<>();
        switch (dimension) {
            case 0:
                if (exp != null) {
                    exp.translate();
                    initVales.add(IrList.getLastOp());
                }
                return initVales;
            case 1:
                for (InitVal initVal : this.initVales) {
                    ArrayList<String> initial = initVal.parserInitVal(0);
                    initVales.addAll(initial);
                }
                return initVales;
            default:
                for (InitVal initVal : this.initVales) {
                    ArrayList<String> initial = initVal.parserInitVal(1);
                    initVales.addAll(initial);
                }
                return initVales;
        }
    }


}
