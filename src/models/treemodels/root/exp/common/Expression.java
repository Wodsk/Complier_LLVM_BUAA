package models.treemodels.root.exp.common;

import models.encode.TCode;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Expression extends Root {
    private final ArrayList<Token> options = new ArrayList<>();

    public TCode getOption(int index) {
        Token option = options.get(index);
        switch (option.getSymbol()) {
            case "MULT":
                return TCode.MULT;
            case "DIV":
                return TCode.DIV;
            case "MOD":
                return TCode.MOD;
            case "LSS":
                return TCode.LSS;
            case "LEQ":
                return TCode.LEQ;
            case "GRE":
                return TCode.GRE;
            case "GEQ":
                return TCode.GEQ;
            case "EQL":
                return TCode.EQL;
            case "NEQ":
                return TCode.NEQ;
            case "PLUS":
                return TCode.PLUS;
            case "MINU":
                return TCode.MINU;
            case "BITAND":
                return TCode.BITAND;
            default:
                return null;
                //exception
        }
    }

    public void addOption(Token option) {
        options.add(option);
    }

    public static boolean isConstExp(String op1, String op2) {
        return isNumber(op1) && isNumber(op2);
    }

    public static boolean isNumber(String op) {
        Pattern pattern = Pattern.compile("-?[0-9]+(\\\\.[0-9]+)?");
        Matcher m = pattern.matcher(op);
        return m.matches();
    }

    public int getDimension() {
        return -1;
    }
}
