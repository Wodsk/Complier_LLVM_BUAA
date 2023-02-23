package models.treemodels.root.func.funcdef;

import models.encode.NCode;
import models.encode.SymbolType;
import models.llvm.IrList;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.exp.ConstExp;
import symbol.ParamItem;
import symbol.SymbolTable;

import java.util.HashMap;

public class FuncFParam extends Root {
    private final Token Ident;
    private int dimension;
    private final HashMap<Integer, ConstExp> constExpMap = new HashMap<>();
    //key is dimension
    public FuncFParam(Token Ident, int line) {
        this.Ident = Ident;
        dimension = 0;
        code = NCode.FuncFParam;
        this.line = line;
    }

    public Token getIdent() {
        return Ident;
    }

    public void setDimension(int dimension, ConstExp constExp) {
        this.dimension = dimension;
        constExpMap.put(dimension, constExp);
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public HashMap<Integer, ConstExp> getConstExpMap() {
        return constExpMap;
    }

    @Override
    public void translate() {
        SymbolTable symbolTable = getSelfTable();
        ParamItem paramItem = null;
        switch (dimension) {
            case 0:
                paramItem = new ParamItem(Ident.getValue(), SymbolType.Variable, Ident.getLine(), 0);
                paramItem.setValueReg(IrList.newReg());
                break;
            case 1:
                paramItem = new ParamItem(Ident.getValue(), SymbolType.Array, Ident.getLine(), 1);
                paramItem.setValueReg(IrList.newReg());
                break;
            default:
                paramItem = new ParamItem(Ident.getValue(), SymbolType.Array, Ident.getLine(), 2);
                constExpMap.get(2).translate();
                paramItem.setD2(IrList.getLastOp());
                paramItem.setValueReg(IrList.newReg());
                break;
        }
        symbolTable.addSymbol(paramItem);
    }
}
