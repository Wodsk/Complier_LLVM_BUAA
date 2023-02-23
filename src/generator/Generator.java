package generator;

import models.encode.SymbolType;
import models.llvm.IrList;
import models.llvm.control.CallIR;
import models.llvm.define.FuncDeclareIR;
import models.treemodels.root.CompUnit;
import models.treemodels.root.block.Stmt;
import symbol.ParamItem;
import symbol.SymbolTable;

import java.util.ArrayList;

public class Generator {
    private final CompUnit compUnit;

    public Generator(CompUnit compUnit) {
        this.compUnit = compUnit;
    }

    public void generator() {
        ArrayList<Integer> levels = new ArrayList<>();
        IrList.getInstance().newBlock();
        FuncDeclareIR getInt = new FuncDeclareIR(false,"getint", levels);
        IrList.getInstance().addIr(getInt);
        levels = new ArrayList<>();
        levels.add(0);
        FuncDeclareIR putInt = new FuncDeclareIR(true, "putint", levels);
        IrList.getInstance().addIr(putInt);
        levels = new ArrayList<>();
        levels.add(0);
        FuncDeclareIR putCh = new FuncDeclareIR(true, "putch", levels);
        IrList.getInstance().addIr(putCh);
        Stmt.cycleDepth = 0;
        //set the loop depth
        compUnit.translate();
    }

    public static void getInt() {
        ArrayList<ParamItem> paramItems = new ArrayList<>();
        ArrayList<String> params = new ArrayList<>();
        CallIR callIR = new CallIR(false, "getint", paramItems, params);
        IrList.getInstance().addIr(callIR);
    }

    public static void putInt(String option) {
        ArrayList<ParamItem> paramItems = new ArrayList<>();
        ArrayList<String> params = new ArrayList<>();
        paramItems.add(new ParamItem("None", SymbolType.Variable, 0, 0));
        params.add(option);
        CallIR callIR = new CallIR(true, "putint", paramItems, params);
        IrList.getInstance().addIr(callIR);
    }

    public static void putCh(String option) {
        ArrayList<ParamItem> paramItems = new ArrayList<>();
        ArrayList<String> params = new ArrayList<>();
        paramItems.add(new ParamItem("None", SymbolType.Variable, 0, 0));
        params.add(option);
        CallIR callIR = new CallIR(true, "putch", paramItems, params);
        IrList.getInstance().addIr(callIR);
    }
}
