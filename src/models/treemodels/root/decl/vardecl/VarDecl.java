package models.treemodels.root.decl.vardecl;

import models.encode.NCode;
import models.treemodels.root.Root;

import java.util.ArrayList;

public class VarDecl extends Root {
    private final ArrayList<VarDef> varDefs = new ArrayList<>();

    public VarDecl(VarDef varDef, int line) {
        code = NCode.VarDecl;
        varDefs.add(varDef);
        this.line = line;
    }

    public void addVarDef(VarDef varDef) {
        varDefs.add(varDef);
    }

    public ArrayList<VarDef> getVarDefs() {
        return varDefs;
    }

    @Override
    public void translate() {
        for (VarDef varDef : varDefs) {
            varDef.translate();
        }
    }
}
