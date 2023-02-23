package models.treemodels.root.exp;

import models.encode.NCode;
import models.treemodels.root.Root;
import models.treemodels.root.exp.common.AddExp;


public class Exp extends Root {
    private final AddExp addExp;

    public Exp(AddExp addExp, int line) {
        code = NCode.Exp;
        this.addExp = addExp;
        this.line = line;
    }

    public AddExp getAddExp() {
        return addExp;
    }

    @Override
    public void translate() {
        addExp.translate();
    }

    public int getDimension() {
        return addExp.getDimension();
    }
}
