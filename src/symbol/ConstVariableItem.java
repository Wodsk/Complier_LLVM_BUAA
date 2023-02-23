package symbol;

import models.encode.SymbolType;

public class ConstVariableItem extends SymbolItem {
    private String initialValue;

    public ConstVariableItem(String name, int line) {
        super(name, SymbolType.Const, line);
    }

    public String getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

}
