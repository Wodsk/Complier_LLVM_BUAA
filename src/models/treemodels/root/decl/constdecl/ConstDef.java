package models.treemodels.root.decl.constdecl;

import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.caculate.GepIR;
import models.llvm.define.GlobalDefIR;
import models.llvm.memory.AllocaIR;
import models.llvm.memory.StoreIR;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.exp.ConstExp;
import symbol.ConstArrayItem;
import symbol.ConstVariableItem;

import java.util.ArrayList;

public class ConstDef extends Root {
    private final Token Ident;
    private int dimension;
    private final ArrayList<ConstExp> dimenValues = new ArrayList<>();
    private ConstInitVal constInitVal;

    public ConstDef(Token Ident, int line) {
        code = NCode.ConstDef;
        this.Ident = Ident;
        this.dimension = 0;
        this.line = line;
    }

    public void setConstInitVal(ConstInitVal constInitVal) {
        this.constInitVal = constInitVal;
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

    @Override
    public void translate() {
        switch (dimension) {
            case 0:
                //only dimension equal 0 here
                ConstVariableItem symbol = new ConstVariableItem(Ident.getValue(), Ident.getLine());
                super.getSelfTable().addSymbol(symbol);
                //get the initialValue from ConstInitVal
                constInitVal.translate();
                symbol.setInitialValue(IrList.getLastOp());
                break;
            case 1:
                // 1 dimension array here
                parseDimension1();
                break;
            default:
                //2 dimension array here
                parseDimension2();
                break;
        }
    }

    private void parseDimension1() {
        ConstArrayItem symbol = new ConstArrayItem(Ident.getValue(), Ident.getLine(), dimension);
        String beginAddress;
        if (super.getSelfTable().getFatherTable() == null) {
            //global const array
            beginAddress = "@" + Ident.getValue();
            dimenValues.get(0).translate();
            symbol.addLength(IrList.getLastOp());// the length of Array
            symbol.setBeginAddress(beginAddress);
            symbol.setInitialValue(constInitVal.parserInitVal(1));
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
            symbol.setInitialValue(constInitVal.parserInitVal(1));
            assignInitial(symbol, beginAddress, length);
        }
        //initial value
        super.getSelfTable().addSymbol(symbol);
    }

    private void parseDimension2() {
        ConstArrayItem symbol = new ConstArrayItem(Ident.getValue(), Ident.getLine(), dimension);
        String beginAddress;
        if (super.getSelfTable().getFatherTable() == null) {
            beginAddress = "@" + Ident.getValue();
            dimenValues.get(0).translate();
            symbol.addLength(IrList.getLastOp());
            dimenValues.get(1).translate();
            symbol.addLength(IrList.getLastOp());
            symbol.setBeginAddress(beginAddress);
            symbol.setInitialValue(constInitVal.parserInitVal(2));
            GlobalDefIR globalDefIR = new GlobalDefIR(symbol);
            IrList.getInstance().addIr(globalDefIR);

        }
        else {
            beginAddress = IrList.newReg();
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
            symbol.setInitialValue(constInitVal.parserInitVal(2));
            assignInitial(symbol, beginAddress, length);
        }
        super.getSelfTable().addSymbol(symbol);
    }

    private void assignInitial(ConstArrayItem symbol, String beginAddress, int length) {
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
