package models.treemodels.root.exp;

import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.caculate.AddIR;
import models.llvm.caculate.GepIR;
import models.llvm.caculate.MulIR;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.exp.common.Expression;
import symbol.*;

import java.util.ArrayList;

public class LVal extends Root {
    private final Token Ident;

    private int dimension;

    private boolean isConst = false;
    private final ArrayList<Exp> exps = new ArrayList<>();

    private boolean needLoad = false;

    public LVal(Token Ident, int line) {
        code = NCode.LVal;
        this.Ident = Ident;
        this.line = line;
    }

    public Token getIdent() {
        return Ident;
    }

    public void addExp(Exp exp) {
        this.exps.add(exp);
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public void translate() {
        SymbolTable symbolTable = super.getSelfTable();
        SymbolItem symbolItem = symbolTable.lookupSymbolItem(Ident);
        if (symbolItem == null) {
            return;
        }
        if (symbolItem instanceof ConstVariableItem) {
            IrList.setLastOp(((ConstVariableItem) symbolItem).getInitialValue());
            dimension = 0;
            isConst = true;
            needLoad = false;
        }
        else if (symbolItem instanceof VariableItem) {
            IrList.setLastOp(((VariableItem) symbolItem).getAddress());
            dimension = 0;
            //just translate the address is enough
            needLoad = true;
        }
        else if (symbolItem instanceof ParamItem){
            dimension = ((ParamItem)symbolItem).getDimension() - exps.size();
            parseParam((ParamItem) symbolItem);
        }
        else if (symbolItem instanceof ConstArrayItem) {
            dimension = ((ConstArrayItem) symbolItem).getDimension() - exps.size();
            parseArray(symbolItem);
            //arr is 2 dimension then arr[1] is 1 dimension, arr[1][1] is 0 dimension
            isConst = true;
        }
        else if (symbolItem instanceof ArrayItem) {
            dimension = ((ArrayItem) symbolItem).getDimension() - exps.size();
            parseArray(symbolItem);
        }
    }

    public int getDimension() {
        return dimension;
    }

    public boolean isConst() {
        return isConst;
    }


    private void parseParam(ParamItem paramItem) {
        switch (paramItem.getDimension()) {
            case 0:
                IrList.setLastOp(paramItem.getAddressReg());
                needLoad = true;
                break;
            case 1:
                if (exps.size() == 1) {
                    exps.get(0).translate();
                    GepIR gepIR = new GepIR(paramItem.getAddressReg(), IrList.getLastOp());
                    IrList.getInstance().addIr(gepIR);
                    needLoad = true;
                }
                else {
                    IrList.setLastOp(paramItem.getAddressReg());
                    needLoad = false;
                }
                break;
            case 2:
                switch (exps.size()) {
                    case 0:
                        IrList.setLastOp(paramItem.getAddressReg());
                        needLoad = false;
                        break;
                    case 1:
                        needLoad = false;
                        exps.get(0).translate();
                        String offset1 = IrList.getLastOp();
                        MulIR mulIR = new MulIR(offset1, paramItem.getD2());
                        IrList.getInstance().addIr(mulIR);
                        GepIR gepIR = new GepIR(paramItem.getAddressReg(), IrList.getLastOp());
                        IrList.getInstance().addIr(gepIR);
                        break;
                    case 2:
                        needLoad = true;
                        exps.get(0).translate();
                        offset1 = IrList.getLastOp();
                        exps.get(1).translate();
                        String offset2 = IrList.getLastOp();
                        mulIR = new MulIR(offset1, paramItem.getD2());
                        IrList.getInstance().addIr(mulIR);
                        AddIR addIR = new AddIR(offset2, IrList.getLastOp());
                        IrList.getInstance().addIr(addIR);
                        gepIR = new GepIR(paramItem.getAddressReg(), IrList.getLastOp());
                        IrList.getInstance().addIr(gepIR);
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }
    private void parseArray(SymbolItem symbolItem) {
        if (symbolItem instanceof ConstArrayItem) {
            ConstArrayItem constArrayItem = (ConstArrayItem) symbolItem;
            switch (exps.size()) {
                case 1:
                    exps.get(0).translate();
                    String index = IrList.getLastOp();
                    if (Expression.isNumber(index)) {
                        needLoad = false;
                        IrList.setLastOp(constArrayItem.getValueByIndex(index));
                    }
                    else {
                        needLoad = true;
                        int len = Integer.parseInt(constArrayItem.getLen());
                        GepIR gepIR = new GepIR(len, constArrayItem.getBeginAddress());
                        IrList.getInstance().addIr(gepIR);
                        gepIR = new GepIR(IrList.getLastOp(), index);
                        IrList.getInstance().addIr(gepIR);
                    }
                    break;
                case 2:
                    exps.get(0).translate();
                    String d1 = IrList.getLastOp();
                    exps.get(1).translate();
                    String d2 = IrList.getLastOp();
                    if (Expression.isNumber(d1) && Expression.isNumber(d2)) {
                        needLoad = false;
                        IrList.setLastOp(constArrayItem.getValueByIndex(d1, d2));
                    }
                    else {
                        needLoad = true;
                        int len = Integer.parseInt(constArrayItem.getLen());
                        GepIR gepIR = new GepIR(len, constArrayItem.getBeginAddress());
                        IrList.getInstance().addIr(gepIR);
                        String base = IrList.getLastOp();
                        MulIR mulIR = new MulIR(d1, constArrayItem.getD2());
                        IrList.getInstance().addIr(mulIR);
                        AddIR addIR = new AddIR(IrList.getLastOp(), d2);
                        IrList.getInstance().addIr(addIR);
                        gepIR = new GepIR(base, IrList.getLastOp());
                        IrList.getInstance().addIr(gepIR);
                    }
                    break;
                default:
                    break;
            }
        }
        else {
            ArrayItem arrayItem = (ArrayItem) symbolItem;
            switch (exps.size()) {
                case 0:
                    needLoad = false;
                    int len = Integer.parseInt(arrayItem.getLen());
                    GepIR gepIR = new GepIR(len, arrayItem.getBeginAddress());
                    IrList.getInstance().addIr(gepIR);
                    //Turn to i32*
                    break;
                case 1:
                    needLoad = true;
                    exps.get(0).translate();
                    String offset = IrList.getLastOp();
                    if (arrayItem.getDimension() == 2) {
                        MulIR mulIR = new MulIR(offset, arrayItem.getD2());
                        IrList.getInstance().addIr(mulIR);
                        offset = IrList.getLastOp();
                        needLoad = false;
                    }
                    len = Integer.parseInt(arrayItem.getLen());
                    gepIR = new GepIR(len, arrayItem.getBeginAddress());
                    IrList.getInstance().addIr(gepIR);
                    gepIR = new GepIR(IrList.getLastOp(), offset);
                    IrList.getInstance().addIr(gepIR);
                    break;
                case 2:
                    needLoad = true;
                    exps.get(0).translate();
                    String offset1 = IrList.getLastOp();
                    exps.get(1).translate();
                    String offset2 = IrList.getLastOp();
                    len = Integer.parseInt(arrayItem.getLen());
                    gepIR = new GepIR(len, arrayItem.getBeginAddress());
                    IrList.getInstance().addIr(gepIR);
                    String base = IrList.getLastOp();
                    MulIR mulIR = new MulIR(offset1, arrayItem.getD2());
                    IrList.getInstance().addIr(mulIR);
                    AddIR addIR = new AddIR(IrList.getLastOp(), offset2);
                    IrList.getInstance().addIr(addIR);
                    gepIR = new GepIR(base, IrList.getLastOp());
                    IrList.getInstance().addIr(gepIR);
                default:
                    break;
            }
        }
    }

    public boolean isNeedLoad() {
        return needLoad;
    }
}
