package symbol;

import models.encode.SymbolType;

import java.util.ArrayList;

public class ConstArrayItem extends SymbolItem {
    private final int dimension;
    private final ArrayList<String> length = new ArrayList<>();
    private String beginAddress;
    private ArrayList<String> initialValue;

    public ConstArrayItem(String name, int line, int dimension) {
        super(name, SymbolType.ConstArray, line);
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

    public String getValueByIndex(String index) {
        return initialValue.get(Integer.parseInt(index));
    }

    public String getValueByIndex(String index1, String index2) {
        int index = Integer.parseInt(index1) * Integer.parseInt(length.get(1)) + Integer.parseInt(index2);
        return initialValue.get(index);
    }
}
