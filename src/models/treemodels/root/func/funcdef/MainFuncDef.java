package models.treemodels.root.func.funcdef;

import errors.Error;
import errors.ErrorList;
import models.encode.NCode;
import models.encode.SymbolType;
import models.llvm.IrList;
import models.llvm.define.DefineEndIR;
import models.llvm.define.FuncDefineIR;
import models.treemodels.root.Root;
import models.treemodels.root.block.Block;
import symbol.FuncItem;
import symbol.ParamItem;

import java.util.ArrayList;

public class MainFuncDef extends Root {
    private final Block block;

    public MainFuncDef(Block block, int line) {
        this.block = block;
        code = NCode.MainFuncDef;
        this.line = line;
    }

    public Block getBlock() {
        return block;
    }

    @Override
    public void translate() {
        FuncDefineIR funcDefineIR = new FuncDefineIR(false, "main");
        IrList.getInstance().addIr(funcDefineIR);
        ArrayList<ParamItem> paramItems = new ArrayList<>();
        funcDefineIR.setParamItems(paramItems);
        IrList.clearCount();
        //IrList.addCount();
        block.translate();
        if (block.checkReturn() == -1) {
            Error error = new Error("g", block.getLine());
            ErrorList.getInstance().addError(error);
        }
        IrList.getInstance().addIr(new DefineEndIR());
    }
}
