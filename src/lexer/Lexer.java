package lexer;

import models.treemodels.leaf.Token;
import io.ReadFile;

import java.io.IOException;

public class Lexer {

    private final ReadFile readFile = ReadFile.getInstance();
    public static int line;
    public Lexer() {
        line = 1;
    }

    public Token analyzeCharacter(int n) throws IOException {
        int next = n;
        String symbol = "";
        String value = "";
        boolean forward = true;
        switch (n) {
            case '\n':
                line = line + 1;
                break;
            case '!':
                next = readFile.readChar();
                if (next == '=') {
                    symbol = "NEQ";
                    value = "!=";
                } else {
                    symbol = "NOT";
                    value = "!";
                    forward = false;
                }
                break;
            case '&':
                next = readFile.readChar();
                if (next == '&') {
                    symbol = "AND";
                    value = "&&";
                } else {
                    forward = false;
                }
                break;
            case '|':
                next = readFile.readChar();
                if (next == '|') {
                    symbol = "OR";
                    value = "||";
                } else {
                    forward = false;
                }
                break;
            case '+':
                symbol = "PLUS";
                value = "+";
                break;
            case '-':
                symbol = "MINU";
                value = "-";
                break;
            case '*':
                symbol = "MULT";
                value = "*";
                break;
            case '/':
                next = readFile.readChar();
                if (next == '/' || next == '*') {
                    readFile.readNote(next);
                    symbol = "";
                    value = "";
                } else {
                    symbol = "DIV";
                    value = "/";
                    forward = false;
                }
                break;
            case '%':
                symbol = "MOD";
                value = "%";
                break;
            case '<':
                next = readFile.readChar();
                if (next == '=') {
                    symbol = "LEQ";
                    value = "<=";
                } else {
                    symbol = "LSS";
                    value = "<";
                    forward = false;
                }
                break;
            case '>':
                next = readFile.readChar();
                if (next == '=') {
                    symbol = "GEQ";
                    value = ">=";
                } else {
                    symbol = "GRE";
                    value = ">";
                    forward = false;
                }
                break;
            case '=':
                next = readFile.readChar();
                if (next == '=') {
                    symbol = "EQL";
                    value = "==";
                } else {
                    symbol = "ASSIGN";
                    value = "=";
                    forward = false;
                }
                break;
            case ';':
                symbol = "SEMICN";
                value = ";";
                break;
            case ',':
                symbol = "COMMA";
                value = ",";
                break;
            case '(':
                symbol = "LPARENT";
                value = "(";
                break;
            case ')':
                symbol = "RPARENT";
                value = ")";
                break;
            case '[':
                symbol = "LBRACK";
                value = "[";
                break;
            case ']':
                symbol = "RBRACK";
                value = "]";
                break;
            case '{':
                symbol = "LBRACE";
                value = "{";
                break;
            case '}':
                symbol = "RBRACE";
                value = "}";
                break;
            default://can't be identified bt Character, so sent to String
                return analyzeString(n);

        }
        if (forward || isEmpty(next)) {
            next = readFile.readNotNull();
        }
        Token token = new Token(symbol, value, next);
        token.setLine(line);
        return token;
    }

    public Token analyzeString(int n) throws IOException {
        int next;
        String symbol;
        String value;
        Token token;
        if (isAlpha(n) || isUnderline(n)) {
            token = readFile.readIdent(n);
            switch (token.getValue()) {
                case "main":
                    symbol = "MAINTK";
                    value = "main";
                    break;
                case "const":
                    symbol = "CONSTTK";
                    value = "const";
                    break;
                case "int":
                    symbol = "INTTK";
                    value = "int";
                    break;
                case "break":
                    symbol = "BREAKTK";
                    value = "break";
                    break;
                case "continue":
                    symbol = "CONTINUETK";
                    value = "continue";
                    break;
                case "if":
                    symbol = "IFTK";
                    value = "if";
                    break;
                case "else":
                    symbol = "ELSETK";
                    value = "else";
                    break;
                case "while":
                    symbol = "WHILETK";
                    value = "while";
                    break;
                case "getint":
                    symbol = "GETINTTK";
                    value = "getint";
                    break;
                case "printf":
                    symbol = "PRINTFTK";
                    value = "printf";
                    break;
                case "return":
                    symbol = "RETURNTK";
                    value = "return";
                    break;
                case "void":
                    symbol = "VOIDTK";
                    value = "void";
                    break;
                case "bitand":
                    symbol = "BITAND";
                    value = "bitand";
                    break;
                default:
                    symbol = "IDENFR";
                    value = token.getValue();
            }
            token.setSymbol(symbol);
            token.setValue(value);
        } else if (isDigit(n)) {
            token = readFile.readDigit(n);
            token.setSymbol("INTCON");
        } else if (n == '"') {
            token = readFile.readString(n);
            token.setSymbol("STRCON");
        } else {
            token = new Token("", "", readFile.readChar());//error
        }
        next = token.getNextChar();
        if (isEmpty(next)) {
            next = readFile.readNotNull();
            token.setNextChar(next);
        }
        token.setLine(line);
        return token;
    }

    private boolean isEmpty(int n) {
        return n == ' '|| n == '\r';
    }

    private boolean isAlpha(int n) {
        return (n >= 'a' && n <= 'z') || (n >= 'A' && n <= 'Z');
    }

    private boolean isDigit(int n) {
        return n >= '0' && n <= '9';
    }

    private boolean isUnderline(int n) {
        return n == '_';
    }
}
