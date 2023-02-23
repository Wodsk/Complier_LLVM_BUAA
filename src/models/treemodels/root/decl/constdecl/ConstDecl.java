package models.treemodels.root.decl.constdecl;

import io.WriteFile;
import models.encode.NCode;
import models.treemodels.root.Root;

import java.io.IOException;
import java.util.ArrayList;

public class ConstDecl extends Root {
    private final ArrayList<ConstDef> constDefs = new ArrayList<>();
    public ConstDecl(ConstDef constDef, int line) {
        code = NCode.ConstDecl;
        constDefs.add(constDef);
        this.line = line;
    }

    public void addConstDef(ConstDef constDef) {
        constDefs.add(constDef);
    }

    public ArrayList<ConstDef> getConstDefs() {
        return constDefs;
    }

    @Override
    public void translate() {
        for (ConstDef constDef : constDefs) {
            constDef.translate();
        }
    }
}
