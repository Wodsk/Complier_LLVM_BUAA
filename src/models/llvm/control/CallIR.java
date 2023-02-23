package models.llvm.control;

import models.llvm.IR;
import models.llvm.IrList;
import symbol.ParamItem;

import java.util.ArrayList;

public class CallIR implements IR {
    private final boolean isVoid;
    private final String name;
    private final ArrayList<String> params;
    private final ArrayList<ParamItem> paramItems;
    private final String resultReg;
    //value is the address of real Params

    public CallIR(boolean isVoid, String name,
                  ArrayList<ParamItem> paramItems, ArrayList<String> params) {
        this.isVoid = isVoid;
        this.name = name;
        this.paramItems = paramItems;
        this.params = params;
        resultReg = isVoid ? "" : IrList.newReg() + " = ";
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("\t");
        s.append(resultReg).append("call ");
        if (isVoid)
            s.append("void @");
        else
            s.append("i32 @");
        s.append(name).append("(");
        for (int i = 0; i < params.size(); i++) {
            /*switch (paramItems.get(i).getDimension()) {
                case 0:
                    s.append("i32 ");
                    break;
                case 1:
                    s.append("i32* ");
                    break;
                default:
                    s.append("[").append(paramItems.get(i).getD2()).append(" x i32]*");
                    break;
            }*/
            ParamItem paramItem = paramItems.get(i);
            if (paramItem.getDimension() == 0) {
                s.append("i32 ");
            }
            else {
                s.append("i32* ");
            }
            s.append(params.get(i));
            if (i != params.size() - 1) {
                s.append(", ");
            }
        }
        s.append(")\n");
        return s.toString();
    }
}
