package symbol;

import models.encode.SymbolType;

public class SymbolItem {
    private final String name;
    private final SymbolType type;

    private final int line;

    public SymbolItem(String name, SymbolType type, int line) {
        this.name = name;
        this.type = type;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public SymbolType getType() {
        return type;
    }


    public int getLine() {
        return line;
    }
}
