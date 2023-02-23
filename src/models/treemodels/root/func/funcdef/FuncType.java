package models.treemodels.root.func.funcdef;

import models.encode.NCode;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;

public class FuncType extends Root {
    private final Token funcType;

    public FuncType(Token funcType) {
        code = NCode.FuncType;
        this.funcType = funcType;
    }

    public Token getFuncType() {
        return funcType;
    }


}
