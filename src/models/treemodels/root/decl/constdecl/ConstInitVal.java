package models.treemodels.root.decl.constdecl;

import models.encode.NCode;
import models.llvm.IrList;
import models.treemodels.root.Root;
import models.treemodels.root.exp.ConstExp;

import java.util.ArrayList;

public class ConstInitVal extends Root {
    private ConstExp constExp = null;
    private final ArrayList<ConstInitVal> constInitVales = new ArrayList<>();

    public ConstInitVal(int line) {
        code = NCode.ConstInitVal;
        this.line = line;
    }

    public ConstInitVal(ConstExp constExp, int line) {
        this.constExp = constExp;
        code = NCode.ConstInitVal;
        this.line = line;
    }

    public ArrayList<ConstInitVal> getConstInitVales() {
        return constInitVales;
    }

    public ConstExp getConstExp() {
        return constExp;
    }

    public void addConstInitVal(ConstInitVal constInitVal) {
        constInitVales.add(constInitVal);
    }

    @Override
    public void translate() {
        //only variable here
        if (constExp != null) {
            constExp.translate();
        }
    }

    public ArrayList<String> parserInitVal(int dimension) {
        ArrayList<String> initVales = new ArrayList<>();
        switch (dimension) {
            case 0:
                if (constExp != null) {
                    constExp.translate();
                    initVales.add(IrList.getLastOp());
                }
                return initVales;
            case 1:
                for (ConstInitVal constInitVal : constInitVales) {
                    ArrayList<String> initial = constInitVal.parserInitVal(0);
                    initVales.addAll(initial);
                }
                return initVales;
            default:
                for (ConstInitVal constInitVal : constInitVales) {
                    ArrayList<String> initial = constInitVal.parserInitVal(1);
                    initVales.addAll(initial);
                }
                return initVales;
        }
    }
}
