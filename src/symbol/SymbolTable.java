package symbol;

import errors.Error;
import errors.ErrorList;
import models.encode.SymbolType;
import models.treemodels.leaf.Token;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    private final HashMap<String, SymbolItem> symbolTable = new HashMap<>();
    private final ArrayList<ParamItem> paramsList = new ArrayList<>();
    //current level Table messages
    private final SymbolTable fatherTable;
    //only use in the block and funcParams, the global is its name
    public SymbolTable(SymbolTable fatherTable) {
        this.fatherTable = fatherTable;
    }

    public SymbolTable getFatherTable() {
        return fatherTable;
    }


    public void addSymbol(SymbolItem symbol) {
        if (symbolExist(symbol.getName())) {
            Error error = new Error("b", symbol.getLine());
            ErrorList.getInstance().addError(error);
            return;
            //exception here
        }
        symbolTable.put(symbol.getName(), symbol);
        if (symbol instanceof ParamItem) {
            paramsList.add((ParamItem) symbol);
        }
    }

    public boolean symbolExist(String symbolName) {
        return symbolTable.containsKey(symbolName);
    }

    public SymbolItem lookupSymbolItem(Token token, SymbolType symbolType) {
        String name = token.getValue();
        if (symbolExist(name)) {
            SymbolItem symbolItem = symbolTable.get(name);
            if (symbolItem.getType().equals(symbolType))
                return symbolItem;
        }
        if (fatherTable != null) {
            return fatherTable.lookupSymbolItem(token, symbolType);
        }
        Error error = new Error("c", token.getLine());
        ErrorList.getInstance().addError(error);
        return null;
        //no definition
    }
    public SymbolItem lookupSymbolItem(Token token) {
        String name = token.getValue();
        if (symbolExist(name)) {
            return symbolTable.get(name);
        }
        if (fatherTable != null) {
            return fatherTable.lookupSymbolItem(token);
        }
        Error error = new Error("c", token.getLine());
        ErrorList.getInstance().addError(error);
        return null;
        //no definition
    }

    public ArrayList<ParamItem> getParamItems() {
        return paramsList;
    }
    public void setParamsAddress(ArrayList<String> addressRegs) {
        int i = 0;
        for (ParamItem symbolItem : paramsList) {
            String address = addressRegs.get(i);
            symbolItem.setAddressReg(address);
            i++;
        }
    }

}
