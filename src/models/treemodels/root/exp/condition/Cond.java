package models.treemodels.root.exp.condition;

import models.encode.NCode;
import models.llvm.BasicBlock;
import models.treemodels.root.Root;
import models.treemodels.root.exp.common.LAndExp;
import models.treemodels.root.exp.common.LOrExp;


public class Cond extends Root {
    private final LOrExp lOrExp;

    public Cond(LOrExp lOrExp, int line) {
        code = NCode.Cond;
        this.lOrExp = lOrExp;
        this.line = line;
    }

    public LOrExp getLOrExp() {
        return lOrExp;
    }

    @Override
    public void translate() {
        lOrExp.translate();
    }

    public void setEnd(String end) {
        LAndExp lastAnd = lOrExp.getLAndExps().get(lOrExp.getLAndExps().size() - 1);
        for (BasicBlock block : lastAnd.getBlocks()) {
            block.getBr().setIfFalse(end);
        }
    }

    public void setBody(String body) {
       for (LAndExp lAndExp : lOrExp.getLAndExps()) {
           BasicBlock lastBlock = lAndExp.getBlocks().get(lAndExp.getBlocks().size() - 1);
           lastBlock.getBr().setIfTrue(body);
       }
    }
}
