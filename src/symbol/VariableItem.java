package symbol;

import models.encode.SymbolType;

public class VariableItem extends SymbolItem {

    private String address;
    private String initialValue;

    public VariableItem(String name, int line) {
        super(name, SymbolType.Variable, line);
        initialValue = null;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    public String getInitialValue() {
        return initialValue;
    }
}
