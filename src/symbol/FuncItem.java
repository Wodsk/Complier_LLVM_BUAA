package symbol;

import models.encode.SymbolType;

import java.util.ArrayList;

public class FuncItem extends SymbolItem {
    private ArrayList<ParamItem> paramItems = new ArrayList<>();
    private final boolean isVoid;

    public FuncItem(String name, int line, boolean isVoid) {
        super(name, SymbolType.Function, line);
        this.isVoid = isVoid;
    }

    public void setParamItems(ArrayList<ParamItem> paramItems) {
        this.paramItems = paramItems;
    }

    public ArrayList<Integer> getLevels() {
        ArrayList<Integer> levels = new ArrayList<>();
        for (ParamItem paramItem : paramItems) {
            levels.add(paramItem.getDimension());
        }
        return levels;
    }

    public ArrayList<ParamItem> getParamItems() {
        return paramItems;
    }

    public boolean isVoid() {
        return isVoid;
    }
}
