package models.treemodels.root.exp.common;

import models.encode.NCode;
import models.llvm.BasicBlock;
import models.llvm.IrList;
import models.llvm.caculate.ZextIR;
import models.llvm.control.BrIR;
import models.llvm.control.IcmpIR;
import models.treemodels.leaf.Token;

import java.util.ArrayList;

public class LAndExp extends Expression {
    private final ArrayList<EqExp> eqExps = new ArrayList<>();
    private final ArrayList<BasicBlock> blocks = new ArrayList<>();

    public LAndExp(EqExp eqExp, int line) {
        code = NCode.LAndExp;
        this.eqExps.add(eqExp);
        this.line = line;
    }

    public ArrayList<EqExp> getEqExps() {
        return eqExps;
    }

    public void addEqExp(EqExp eqExp, Token option) {
        super.addOption(option);
        this.eqExps.add(eqExp);
    }

    @Override
    public void translate() {
        IrList irList = IrList.getInstance();
        irList.newBlock();
        blocks.add(irList.getNowBlock());
        eqExps.get(0).translate();
        irList.addIr(new IcmpIR("ne", IrList.getLastOp(), "0"));
        BrIR brIR = new BrIR(IrList.getLastOp(), 2);
        irList.getNowBlock().setBr(brIR);
        for (int i = 1; i < eqExps.size(); i++) {
            irList.newBlock();
            BasicBlock nowBLock = irList.getNowBlock();
            blocks.add(nowBLock);
            BasicBlock lastBlock = getForeBlock();
            lastBlock.getBr().setIfTrue(nowBLock.getLabel());
            eqExps.get(i).translate();
            irList.addIr(new IcmpIR("ne", IrList.getLastOp(), "0"));
            brIR = new BrIR(IrList.getLastOp(), 2);
            nowBLock.setBr(brIR);
        }
    }

    public ArrayList<BasicBlock> getBlocks() {
        return blocks;
    }

    private BasicBlock getForeBlock() {
        return blocks.get(blocks.size()-2);
    }
}
