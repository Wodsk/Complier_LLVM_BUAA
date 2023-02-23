package models.treemodels.root.decl;

import models.encode.NCode;
import models.treemodels.root.Root;
import models.treemodels.root.decl.constdecl.ConstDecl;
import models.treemodels.root.decl.vardecl.VarDecl;

public class Decl extends Root {
    private ConstDecl constDecl = null;
    private VarDecl varDecl = null;
    private  final boolean isConst;

    public Decl(ConstDecl constDecl, int line) {
        this.constDecl = constDecl;
        this.isConst = true;
        code = NCode.Decl;
    }

    public Decl(VarDecl varDecl, int line) {
        this.varDecl = varDecl;
        this.isConst = false;
        code = NCode.Decl;
        this.line = line;
    }

    public boolean isConst() {
        return isConst;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public ConstDecl getConstDecl() {
        return constDecl;
    }

    @Override
    public void translate() {
        if (isConst) {
            constDecl.translate();
        }
        else {
            varDecl.translate();
        }
    }
}
