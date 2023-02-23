package models.treemodels.root.block;

import models.encode.NCode;
import models.treemodels.root.Root;
import models.treemodels.root.decl.Decl;

public class BlockItem extends Root {
    private Decl decl;
    private Stmt stmt;

    private final boolean isDecl;

    public BlockItem(Decl decl, int line) {
        this.decl = decl;
        isDecl = true;
        code = NCode.BlockItem;
        this.line = line;
    }

    public BlockItem(Stmt stmt, int line) {
        this.stmt = stmt;
        isDecl = false;
        code = NCode.BlockItem;
        this.line = line;
    }


    public Decl getDecl() {
        return decl;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public boolean isDecl() {
        return isDecl;
    }

    @Override
    public void translate() {
        if (isDecl) {
            decl.translate();
        }
        else {
            stmt.translate();
        }
    }

    public int checkReturn() {
        if (stmt != null) {
            return stmt.checkReturn();
        }
        return -1;
    }
}
