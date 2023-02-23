package models.treemodels.root.decl.vardecl;

import generator.Generator;
import models.encode.NCode;
import models.llvm.caculate.GepIR;
import models.llvm.define.GlobalDefIR;
import models.llvm.IrList;
import models.llvm.memory.AllocaIR;
import models.llvm.memory.StoreIR;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.exp.ConstExp;
import symbol.ArrayItem;
import symbol.ConstArrayItem;
import symbol.SymbolTable;
import symbol.VariableItem;

import java.util.ArrayList;

import static models.llvm.IrList.getLastOp;
import static models.llvm.IrList.newReg;

public class VarDef extends Root {
    private final Token Ident;
    private int dimension;
    private final ArrayList<ConstExp> dimenValues = new ArrayList<>();
    private InitVal initVal = null;

    private boolean isGetInt = false;

    public VarDef(Token Ident, int line) {
        this.dimension = 0;
        this.Ident = Ident;
        code = NCode.VarDef;
        this.line = line;
    }

    public int getDimension() {
        return dimension;
    }

    public void addDimenValue(ConstExp constExp) {
        this.dimension++;
        dimenValues.add(constExp);
    }

    public ArrayList<ConstExp> getDimenValues() {
        return dimenValues;
    }

    public Token getIdent() {
        return Ident;
    }

    public void setGetInt(boolean getInt) {
        isGetInt = getInt;
    }

    public void setInitVal(InitVal initVal) {
        this.initVal = initVal;
    }

    @Override
    public void translate() {
        SymbolTable symbolTable = super.getSelfTable();
        if (dimension == 0) {
            parserVariable(symbolTable);
        }
        else {
           parserArray(symbolTable);
        }
    }

    private void parserVariable(SymbolTable symbolTable) {
        VariableItem variableItem = new VariableItem(Ident.getValue(), Ident.getLine());
        String address;
        if (symbolTable.getFatherTable() == null) {
            address = "@" + Ident.getValue();
            String value;
            if (initVal != null) {
                initVal.translate();
                value = IrList.getLastOp();
            }
            else {
                value = "0";
            }
            variableItem.setInitialValue(value);
            GlobalDefIR globalDefIR = new GlobalDefIR(variableItem);
            IrList.getInstance().addIr(globalDefIR);
        }
        else {
            address = newReg();
            AllocaIR allocaIR = new AllocaIR(address);
            IrList.getInstance().addIr(allocaIR);
            if (initVal != null) {
                initVal.translate();
                //get the initVal instructions
                StoreIR storeIR = new StoreIR(getLastOp(), address);
                IrList.getInstance().addIr(storeIR);
            }
            else if (isGetInt) {
                Generator.getInt();
                StoreIR storeIR = new StoreIR(getLastOp(), address);
                IrList.getInstance().addIr(storeIR);
            }
        }
        variableItem.setAddress(address);
        symbolTable.addSymbol(variableItem);
    }

    private void parserArray(SymbolTable symbolTable) {
        //still distinguish global or not
        ArrayItem symbol = new ArrayItem(Ident.getValue(), Ident.getLine(), dimension);
        if (dimension == 1) {
            parseDimension1(symbolTable, symbol);
        }
        else {
            parseDimension2(symbolTable, symbol);
        }
    }

    private void parseDimension1(SymbolTable symbolTable, ArrayItem symbol) {
        String beginAddress;
        if (super.getSelfTable().getFatherTable() == null) {
            //global const array
            beginAddress = "@" + Ident.getValue();
            dimenValues.get(0).translate();
            symbol.addLength(IrList.getLastOp());// the length of Array
            symbol.setBeginAddress(beginAddress);
            if (initVal != null) {
                symbol.setInitialValue(initVal.parserInitVal(1));
            }
            GlobalDefIR globalDefIR = new GlobalDefIR(symbol);
            IrList.getInstance().addIr(globalDefIR);
        }
        else {
            beginAddress = IrList.newReg();
            dimenValues.get(0).translate();
            AllocaIR allocaIR = new AllocaIR(beginAddress, IrList.getLastOp());
            IrList.getInstance().addIr(allocaIR);
            int length = Integer.parseInt(IrList.getLastOp());
            symbol.addLength(IrList.getLastOp());// the length of Array
            symbol.setBeginAddress(beginAddress);
            if (initVal != null) {
                symbol.setInitialValue(initVal.parserInitVal(1));
                assignInitial(symbol, beginAddress, length);
            }
        }
        //initial value
        symbolTable.addSymbol(symbol);
    }

    private void parseDimension2(SymbolTable symbolTable, ArrayItem symbol) {
        String beginAddress;
        if (super.getSelfTable().getFatherTable() == null) {
            beginAddress = "@" + Ident.getValue();
            dimenValues.get(0).translate();
            symbol.addLength(IrList.getLastOp());
            dimenValues.get(1).translate();
            symbol.addLength(IrList.getLastOp());
            symbol.setBeginAddress(beginAddress);
            if (initVal != null)  {
                symbol.setInitialValue(initVal.parserInitVal(2));
            }
            GlobalDefIR globalDefIR = new GlobalDefIR(symbol);
            IrList.getInstance().addIr(globalDefIR);

        }
        else {
            beginAddress = newReg();
            dimenValues.get(0).translate();
            String d1 = IrList.getLastOp();
            symbol.addLength(d1);
            dimenValues.get(1).translate();
            String d2 = IrList.getLastOp();
            symbol.addLength(d2);
            AllocaIR allocaIR = new AllocaIR(beginAddress, d1, d2);
            IrList.getInstance().addIr(allocaIR);
            int length = Integer.parseInt(symbol.getLen());
            symbol.setBeginAddress(beginAddress);
            if (initVal != null) {
                symbol.setInitialValue(initVal.parserInitVal(2));
                assignInitial(symbol, beginAddress, length);
            }
        }
        symbolTable.addSymbol(symbol);
    }
    private void assignInitial(ArrayItem symbol, String beginAddress, int length) {
        GepIR gepIR = new GepIR(length, beginAddress);
        IrList.getInstance().addIr(gepIR);
        String base = IrList.getLastOp();
        ArrayList<String> initials = symbol.getValues();
        StoreIR storeIR = new StoreIR(initials.get(0), base);
        IrList.getInstance().addIr(storeIR);
        for (int i = 1; i < length; i++) {
            gepIR = new GepIR(base, i);
            IrList.getInstance().addIr(gepIR);
            storeIR = new StoreIR(initials.get(i), IrList.getLastOp());
            IrList.getInstance().addIr(storeIR);
        }
    }
}
