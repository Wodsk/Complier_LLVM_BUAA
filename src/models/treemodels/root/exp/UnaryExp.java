package models.treemodels.root.exp;

import errors.Error;
import errors.ErrorList;
import models.encode.NCode;
import models.encode.SymbolType;
import models.llvm.IrList;
import models.llvm.caculate.AddIR;
import models.llvm.caculate.SubIR;
import models.llvm.caculate.ZextIR;
import models.llvm.control.CallIR;
import models.llvm.control.IcmpIR;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.exp.common.Expression;
import models.treemodels.root.func.FuncRParams;
import symbol.FuncItem;
import symbol.ParamItem;
import symbol.SymbolItem;

import java.util.ArrayList;

public class UnaryExp extends Root {
    private Token Ident = null;
    private Token unaryOp = null;
    private UnaryExp unaryExp = null;
    private PrimaryExp primaryExp = null;
    private FuncRParams funcRParams = null;


    public UnaryExp(PrimaryExp primaryExp, int line) {
        code = NCode.UnaryExp;
        this.primaryExp = primaryExp;
        this.line = line;
    }

    public UnaryExp(Token Ident, int line) {
        code = NCode.UnaryExp;
        this.Ident = Ident;
        this.line = line;
    }

    public UnaryExp(Token unaryOp, UnaryExp unaryExp, int line) {
        code = NCode.UnaryExp;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
        this.line = line;
    }


    public Token getIdent() {
        return Ident;
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }

    public Token getUnaryOp() {
        return unaryOp;
    }

    public UnaryExp getUnaryExp() {
        return unaryExp;
    }

    public void setFuncRParams(FuncRParams funcRParams) {
        this.funcRParams = funcRParams;
    }

    @Override
    public void translate() {
        if (primaryExp != null) {
            primaryExp.translate();
        }
        else if (unaryOp != null) {
            unaryExp.translate();
            String op1 = "0";
            String op2 = IrList.getLastOp();
            int result;
            switch (unaryOp.getSymbol()) {
                case "PLUS":
                    if (Expression.isNumber(op2)) {
                       result = Integer.parseInt(op1) + Integer.parseInt(op2);
                       IrList.setLastOp(String.valueOf(result));
                    }
                    else {
                        AddIR addIR = new AddIR(op1, op2);
                        IrList.getInstance().addIr(addIR);
                    }
                    break;
                case "MINU":
                    if (Expression.isNumber(op2)) {
                        result = Integer.parseInt(op1) - Integer.parseInt(op2);
                        IrList.setLastOp(String.valueOf(result));
                    } else {
                        SubIR subIR = new SubIR(op1, op2);
                        IrList.getInstance().addIr(subIR);
                    }
                    break;
                case "NOT":
                    IcmpIR icmpIR = new IcmpIR("eq", op2, op1);
                    IrList.getInstance().addIr(icmpIR);
                    ZextIR zextIR = new ZextIR(IrList.getLastOp());
                    IrList.getInstance().addIr(zextIR);
                default:
                    break;
            }
        }
        else {
            ArrayList<String> params = new ArrayList<>();
            if (funcRParams != null) {
                funcRParams.translate();
                params = funcRParams.getParams();
            }
            SymbolItem symbolItem = getSelfTable().lookupSymbolItem(Ident, SymbolType.Function);
            if (symbolItem == null) {
                return;
            }
            ArrayList<Integer> levels = ((FuncItem)symbolItem).getLevels();
            ArrayList<ParamItem> paramItems = ((FuncItem) symbolItem).getParamItems();
            ArrayList<Integer> paramLevels = new ArrayList<>();
            if (funcRParams != null) {
                paramLevels = funcRParams.getParamLevels();
            }
            if (paramLevels.size() != levels.size()) {
                Error error = new Error("d", Ident.getLine());
                ErrorList.getInstance().addError(error);
            }
            else if (!levels.equals(paramLevels)) {
                Error error = new Error("e", Ident.getLine());
                ErrorList.getInstance().addError(error);
            }
            boolean isVoid = ((FuncItem)symbolItem).isVoid();
            CallIR callIR = new CallIR(isVoid, Ident.getValue(), paramItems, params);
            IrList.getInstance().addIr(callIR);
        }
    }

    public int getDimension() {
        if (primaryExp != null) {
            return primaryExp.getDimension();
        }
        else if (unaryOp != null) {
            return 0;
        }
        else {
            SymbolItem symbolItem = getSelfTable().lookupSymbolItem(Ident, SymbolType.Function);
            if (symbolItem == null) {
                return -1;
            }
            boolean isVoid = ((FuncItem)symbolItem).isVoid();
            return isVoid ? -1 : 0;
        }
    }
}
