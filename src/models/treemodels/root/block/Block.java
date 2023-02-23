package models.treemodels.root.block;

import io.WriteFile;
import models.encode.NCode;
import models.llvm.IrList;
import models.treemodels.root.Root;

import java.io.IOException;
import java.util.ArrayList;

public class Block extends Root {
    private final ArrayList<BlockItem> blockItems = new ArrayList<>();
    public Block() {
      code = NCode.Block;
    }

    public void addBlockItem(BlockItem blockItem) {
        blockItems.add(blockItem);
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    @Override
    public void translate() {
        for (BlockItem blockItem : blockItems) {
            blockItem.translate();
        }
    }

    public int checkReturn() {
        //This return mean return a value
        for (BlockItem blockItem : blockItems) {
            if (blockItem.checkReturn() != -1) {
                return blockItem.checkReturn();
            }
        }
        return -1;
    }

    public void setLine(int line) {
        this.line = line;
    }
    //block line is the end of block
}
