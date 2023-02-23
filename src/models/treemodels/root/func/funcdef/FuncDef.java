package models.treemodels.root.func.funcdef;

import errors.Error;
import errors.ErrorList;
import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.control.ReturnIR;
import models.llvm.define.DefineEndIR;
import models.llvm.define.FuncDefineIR;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.block.Block;
import symbol.FuncItem;
import symbol.ParamItem;
import symbol.SymbolTable;

import java.util.ArrayList;

public class FuncDef extends Root {
    private final FuncType funcType;
    private final Token Ident;
    private Block block;

    private FuncFParams funcFParams = null;

    public FuncDef(FuncType funcType, Token Ident, int line) {
        this.funcType = funcType;
        this.Ident = Ident;
        code = NCode.FuncDef;
        this.line = line;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public FuncType getFuncType() {
        return funcType;
    }

    public Token getIdent() {
        return Ident;
    }

    public Block getBlock() {
        return block;
    }

    public FuncFParams getFuncFParams() {
        return funcFParams;
    }

    public void setFuncFParams(FuncFParams funcFParams) {
        this.funcFParams = funcFParams;
    }

    @Override
    public void translate() {
        boolean isVoid = funcType.getFuncType().getSymbol().equals("VOIDTK");
        FuncDefineIR funcDefineIR = new FuncDefineIR(isVoid, Ident.getValue());
        IrList.getInstance().addIr(funcDefineIR);
        FuncItem funcItem = new FuncItem(Ident.getValue(), Ident.getLine(), isVoid);
        ArrayList<ParamItem> paramItems = new ArrayList<>();
        IrList.clearCount();
        if (funcFParams != null) {
            funcFParams.translate();
            SymbolTable symbolTable = funcFParams.getSelfTable();
            paramItems = symbolTable.getParamItems();
        }
        funcItem.setParamItems(paramItems);
        getSelfTable().addSymbol(funcItem);
        block.translate();
        int returnLine = block.checkReturn();
        if (isVoid && returnLine != -1) {
            Error error = new Error("f", returnLine);
            ErrorList.getInstance().addError(error);
        }
        else if (!isVoid && returnLine == -1) {
            Error error = new Error("g", block.getLine());
            ErrorList.getInstance().addError(error);
        }
        funcDefineIR.setParamItems(paramItems);
        if (isVoid) {
            ReturnIR returnIR = new ReturnIR(null);
            IrList.getInstance().addIr(returnIR);
        }//return void
        IrList.getInstance().addIr(new DefineEndIR());
    }
}
