package models.treemodels.root.func.funcdef;

import models.encode.NCode;
import models.llvm.IrList;
import models.llvm.memory.AllocaIR;
import models.llvm.memory.StoreIR;
import models.treemodels.root.Root;
import symbol.ParamItem;

import java.util.ArrayList;

public class FuncFParams extends Root {
    private final ArrayList<FuncFParam> funcFParams = new ArrayList<>();

    public FuncFParams(FuncFParam funcFParam, int line) {
        code = NCode.FuncFParams;
        this.funcFParams.add(funcFParam);
        this.line = line;
    }

    public FuncFParams(int line) {
        code = NCode.FuncFParams;
        this.line = line;
    }

    public void addFuncFParam(FuncFParam funcFParam) {
        this.funcFParams.add(funcFParam);
    }

    public ArrayList<FuncFParam> getFuncFParams() {
        return funcFParams;
    }

    @Override
    public void translate() {
        for (FuncFParam funcFParam : funcFParams) {
            funcFParam.translate();
        }
        //IrList.addCount();
        ArrayList<ParamItem> params = getSelfTable().getParamItems();
        ArrayList<String> addressRegs = new ArrayList<>();
        for (ParamItem paramItem : params) {
            if (paramItem.getDimension() == 0) {
                String address = IrList.newReg();
                addressRegs.add(address);
                AllocaIR allocaIR = new AllocaIR(address);
                IrList.getInstance().addIr(allocaIR);
                StoreIR storeIR = new StoreIR(paramItem.getValueReg(), IrList.getLastOp());
                IrList.getInstance().addIr(storeIR);
            } else {
                addressRegs.add(paramItem.getValueReg());
            }
        }
        getSelfTable().setParamsAddress(addressRegs);
    }
}
