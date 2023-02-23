package models.treemodels.root.exp;

import models.encode.NCode;
import models.treemodels.root.Root;
import models.treemodels.root.exp.common.AddExp;

public class ConstExp extends Root {
    private final AddExp addExp;

    public ConstExp(AddExp addExp, int line) {
        code = NCode.ConstExp;
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
}
