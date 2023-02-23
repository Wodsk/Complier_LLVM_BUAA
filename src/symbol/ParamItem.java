package symbol;

import models.encode.SymbolType;

public class ParamItem extends SymbolItem {
    private String valueReg;
    private String addressReg;
    private final int dimension;
    private String d2;


    public ParamItem(String name, SymbolType type, int line, int dimension) {
        super(name, type, line);
        this.dimension = dimension;
    }

    public int getDimension() {
        return dimension;
    }
    public String getValueReg() {
        return valueReg;
    }

    public void setValueReg(String valueReg) {
        this.valueReg = valueReg;
    }

    public void setAddressReg(String addressReg) {
        this.addressReg = addressReg;
    }

    public String getAddressReg() {
        return addressReg;
    }


    public String getD2() {
        return d2;
    }

    public void setD2(String d2) {
        this.d2 = d2;
    }

}
