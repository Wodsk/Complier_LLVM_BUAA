package models.treemodels.root.exp.common;

import models.encode.NCode;
import models.llvm.BasicBlock;
import models.llvm.IrList;
import models.llvm.control.BrIR;
import models.llvm.control.IcmpIR;
import models.treemodels.leaf.Token;

import java.util.ArrayList;

public class LOrExp extends Expression {
    private final ArrayList<LAndExp> lAndExps = new ArrayList<>();

    private final ArrayList<BasicBlock> blocks = new ArrayList<>();

    private BasicBlock turnToAnd = null;
    public LOrExp(LAndExp lAndExp, int line) {
        code = NCode.LOrExp;
        this.lAndExps.add(lAndExp);
        this.line = line;
    }


    public ArrayList<LAndExp> getLAndExps() {
        return lAndExps;
    }

    public void addLAndExp(LAndExp lAndExp, Token option) {
        super.addOption(option);
        this.lAndExps.add(lAndExp);
    }

    @Override
    public void translate() {
        IrList irList = IrList.getInstance();
        irList.newBlock();
        turnToAnd = irList.getNowBlock();
        turnToAnd.setBr(new BrIR());
        lAndExps.get(0).translate();
        ArrayList<BasicBlock> andBlocks = lAndExps.get(0).getBlocks();
        turnToAnd.getBr().setDest(andBlocks.get(0).getLabel());
        //un-condition shift to and begin
        for (int i = 1; i < lAndExps.size(); i++) {
            lAndExps.get(i).translate();
            String label = lAndExps.get(i).getBlocks().get(0).getLabel();
            writeBackAnds(lAndExps.get(i - 1), label);
        }
    }

    private void writeBackAnds(LAndExp foreAnd, String label) {
        ArrayList<BasicBlock> andBlocks = foreAnd.getBlocks();
        for (BasicBlock andBlock : andBlocks) {
            andBlock.getBr().setIfFalse(label);
        }
    }

    public BasicBlock getTurnToAnd() {
        return turnToAnd;
    }


}

