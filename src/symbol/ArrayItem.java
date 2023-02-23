package symbol;

import models.encode.SymbolType;

import java.util.ArrayList;

public class ArrayItem extends SymbolItem {
    private final int dimension;
    private final ArrayList<String> length = new ArrayList<>();
    private ArrayList<String> initialValue = new ArrayList<>();

    private String beginAddress;

    public ArrayItem(String name, int line, int dimension) {
        super(name, SymbolType.Array, line);
        this.dimension = dimension;
    }

    public void addLength(String length) {
        this.length.add(length);
    }

    public void setInitialValue(ArrayList<String> initialValue) {
        this.initialValue = initialValue;
    }

    public int getDimension() {
        return dimension;
    }

    public void setBeginAddress(String beginAddress) {
        this.beginAddress = beginAddress;
    }

    public String getBeginAddress() {
        return beginAddress;
    }

    public String getInitialValue() {
        if (initialValue.size() == 0) {
            return "zeroinitializer\n";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            for (int i = 0; i < initialValue.size(); i++) {
                sb.append("i32 ").append(initialValue.get(i));
                if (i != initialValue.size() - 1) {
                    sb.append(", ");
                } else {
                    sb.append("]\n");
                }
            }
            return sb.toString();
        }
    }

    public ArrayList<String> getValues() {
        return initialValue;
    }

    public String getLen() {
        if (length.size() == 1) {
            return length.get(0);
        }
        else {
            int ans = Integer.parseInt(length.get(0)) * Integer.parseInt(length.get(1));
            return String.valueOf(ans);
        }
    }

    public String getD2() {
        return length.get(1);
    }
}
