package models.llvm.define;

import models.llvm.IR;
import symbol.ArrayItem;
import symbol.ConstArrayItem;
import symbol.SymbolItem;
import symbol.VariableItem;

public class GlobalDefIR implements IR {

    private final SymbolItem item;

    public GlobalDefIR(SymbolItem item) {
        this.item = item;
    }

    @Override
    public String toString() {
        switch (item.getType()) {
            case Variable:
                VariableItem variableItem = (VariableItem) item;
                return variableItem.getAddress() + " = dso_local global i32 "
                        + variableItem.getInitialValue() + "\n";
            case ConstArray:
                ConstArrayItem constArrayItem = (ConstArrayItem) item;
                return constArrayItem.getBeginAddress() +  " = dso_local constant ["
                        + constArrayItem.getLen() + " x i32] " +
                        constArrayItem.getInitialValue();
            case Array:
                ArrayItem arrayItem = (ArrayItem) item;
                return arrayItem.getBeginAddress() +  " = dso_local global ["
                        + arrayItem.getLen() + " x i32] " +
                        arrayItem.getInitialValue();
            default:
                return "";
        }
    }
}
