package models.llvm;

import java.io.IOException;
import java.util.ArrayList;

public class IrList {
    private static final IrList IR_LIST = new IrList();
    private final ArrayList<BasicBlock> blockList = new ArrayList<>();
    private BasicBlock nowBlock;
    private static int regCount = 0;

    private static int blockCount = 0;
    private static String lastOp;


    public static IrList getInstance() {
        return IR_LIST;
    }

    public void addIr(IR ir) {
        nowBlock.addIr(ir);
    }

    public static void clearCount() {
        IrList.regCount = 0;
    }

    /*public static void addCount() {
        IrList.regCount++;
    }*/

    public static String newReg() {
        lastOp =  "%x" + IrList.regCount;
        ++IrList.regCount;
        return lastOp;
    }

    public void newBlock() {
        BasicBlock block = new BasicBlock();
        block.setLabel("block" + IrList.blockCount);
        ++IrList.blockCount;
        nowBlock = block;
        blockList.add(block);
    }

    public BasicBlock getNowBlock() {
        return nowBlock;
    }

    public static void setLastOp(String lastOp) {
        IrList.lastOp = lastOp;
    }

    public static String getLastOp() {
        return lastOp;
    }

    public void print() throws IOException {
        for (BasicBlock block : blockList) {
            block.print();
        }
    }
}
