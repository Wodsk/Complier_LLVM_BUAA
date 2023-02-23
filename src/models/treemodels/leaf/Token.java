package models.treemodels.leaf;

import java.util.Objects;

public class Token {

    private String symbol;
    private String value;
    private int nextChar;
    private boolean valid;

    private int line;

    public Token(String symbol, String value, int nextChar) {
        this.symbol = symbol;
        this.value = value;
        this.nextChar = nextChar;
        this.valid = !(Objects.equals(symbol, ""));
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
        this.valid = !(Objects.equals(symbol, ""));
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getNextChar() {
        return nextChar;
    }

    public void setNextChar(int nextChar) {
        this.nextChar = nextChar;
    }

    public boolean isValid() {
        return valid;
    }


    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
