package models.treemodels.root;

import models.encode.NCode;
import models.treemodels.root.decl.Decl;
import models.treemodels.root.func.funcdef.FuncDef;
import models.treemodels.root.func.funcdef.MainFuncDef;

import java.util.ArrayList;

public class CompUnit extends Root {
    private final ArrayList<Decl> decls = new ArrayList<>();
    private final ArrayList<FuncDef> funcDefs = new ArrayList<>();
    private MainFuncDef mainFuncDef;

    public CompUnit() {
        code = NCode.CompUnit;
    }

    public void setMainFuncDef(MainFuncDef mainFuncDef) {
        this.mainFuncDef = mainFuncDef;
    }
    public void addDecl(Decl decl) {
        decls.add(decl);
    }

    public void addFuncDef(FuncDef funcDef) {
        funcDefs.add(funcDef);
    }

    public MainFuncDef getMainFuncDef() {
        return mainFuncDef;
    }

    public ArrayList<FuncDef> getFuncDefs() {
        return funcDefs;
    }

    public ArrayList<Decl> getDecls() {
        return decls;
    }

    @Override
    public void translate() {
        for (Decl decl : decls) {
            decl.translate();
        }
        for (FuncDef funcDef : funcDefs) {
            funcDef.translate();
        }
        mainFuncDef.translate();
    }
}
