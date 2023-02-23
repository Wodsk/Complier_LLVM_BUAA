package models.llvm.define;

import models.llvm.IR;
import symbol.ParamItem;

import java.util.ArrayList;

public class FuncDefineIR implements IR {
    private final boolean isVoid;
    private final String name;
    private ArrayList<ParamItem> paramItems;

    public FuncDefineIR(boolean isVoid, String name) {
        this.isVoid = isVoid;
        this.name = name;
    }

    public void setParamItems(ArrayList<ParamItem> paramItems) {
        this.paramItems = paramItems;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder("define dso_local ");
        if (isVoid)
            s.append("void @");
        else
            s.append("i32 @");
        s.append(name).append("(");
        for (int i = 0; i < paramItems.size(); i++) {
            ParamItem paramItem = paramItems.get(i);
            int integer = paramItems.get(i).getDimension();
            /*switch (integer) {
                case 0:
                    s.append("i32 ").append(paramItem.getValueReg());
                    break;
                case 1:
                    s.append("i32* ").append(paramItem.getValueReg());
                    break;
                default:
                    s.append("[").append(paramItem.getD2()).append(" x i32]* ").append(paramItem.getValueReg());
                    break;
            }*/
            if (integer == 0) {
                s.append("i32 ").append(paramItem.getValueReg());
            }
            else {
                s.append("i32* ").append(paramItem.getValueReg());
            }
            if (i != paramItems.size() - 1) {
                s.append(", ");
            }
        }
        s.append(") {\n");
        return s.toString();
    }
}
