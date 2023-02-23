package models.treemodels.root.block;

import errors.Error;
import errors.ErrorList;
import generator.Generator;
import models.encode.NCode;
import models.encode.StmtType;
import models.llvm.BasicBlock;
import models.llvm.IrList;
import models.llvm.control.BrIR;
import models.llvm.control.ReturnIR;
import models.llvm.memory.StoreIR;
import models.treemodels.leaf.Token;
import models.treemodels.root.Root;
import models.treemodels.root.exp.condition.Cond;
import models.treemodels.root.exp.Exp;
import models.treemodels.root.exp.LVal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class Stmt extends Root {
    private final StmtType type;
    private LVal lval = null;
    private final ArrayList<Exp> exps = new ArrayList<>();
    private Block block = null;
    private Cond cond = null;

    public static int cycleDepth;

    public static Stack<String> loopBeginStack = new Stack<>();

    public static HashMap<Integer, ArrayList<BrIR>> breakMap = new HashMap<>();


    private Token formatString = null;
    private final ArrayList<Stmt> stmts = new ArrayList<>();

    public Stmt(LVal lVal, Exp exp, int line) {
        type = StmtType.Assignment;
        this.lval = lVal;
        exps.add(exp);
        code = NCode.Stmt;
        this.line = line;
    }

    public Stmt(StmtType type, int line) {
        this.type = type;
        code = NCode.Stmt;
        //used for empty, break, continue, return void
        this.line = line;
    }

    public Stmt(Block block, int line) {
        code = NCode.Stmt;
        type = StmtType.Block;
        this.block = block;
        this.line = line;
    }

    public Stmt(Exp exp, boolean isReturn, int line) {
        code = NCode.Stmt;
        if (isReturn)
            type = StmtType.Return;
        else
            type = StmtType.Exp;
        exps.add(exp);
        this.line = line;
    }

    public Stmt(Cond cond, Stmt stmt, boolean isCondition, int line) {
        code = NCode.Stmt;
        if (isCondition)
            type = StmtType.Condition;
        else
            type = StmtType.Loop;
        this.cond = cond;
        stmts.add(stmt);
        this.line = line;
    }

    public Stmt(LVal lval, int line) {
        code = NCode.Stmt;
        type = StmtType.Input;
        this.lval = lval;
        this.line = line;
    }

    public Stmt(Token formatString, int line) {
        code = NCode.Stmt;
        type = StmtType.Print;
        this.formatString = formatString;
        this.line = line;
    }

    public StmtType getType() {
        return type;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }

    public Block getBlock() {
        return block;
    }

    public Cond getCond() {
        return cond;
    }

    public LVal getLVal() {
        return lval;
    }

    public ArrayList<Stmt> getStmts() {
        return stmts;
    }

    public Token getFormatString() {
        return formatString;
    }

    public void addExp(Exp exp) {
        this.exps.add(exp);
    }

    public void addStmt(Stmt stmt) {
        stmts.add(stmt);
    }

    @Override
    public void translate() {
        StoreIR storeIR;
        String address;
        switch (type) {
            case Assignment:
                lval.translate();
                if (lval.isConst()) {
                    Error error = new Error("h", lval.getLine());
                    ErrorList.getInstance().addError(error);
                }
                address = IrList.getLastOp();
                exps.get(0).translate();
                String option = IrList.getLastOp();
                storeIR = new StoreIR(option, address);
                IrList.getInstance().addIr(storeIR);
                break;
            case Exp:
                exps.get(0).translate();
                break;
            case Block:
                block.translate();
                break;
            case Return:
                //must have return
                if (!exps.isEmpty()) {
                    exps.get(0).translate();
                    ReturnIR returnIR= new ReturnIR(IrList.getLastOp());
                    IrList.getInstance().addIr(returnIR);
                }
                else {
                    ReturnIR returnIR = new ReturnIR(null);
                    IrList.getInstance().addIr(returnIR);
                }
                break;
            case Input:
                lval.translate();
                if (lval.isConst()) {
                    Error error = new Error("h", lval.getLine());
                    ErrorList.getInstance().addError(error);
                }
                address = IrList.getLastOp();
                Generator.getInt();
                storeIR = new StoreIR(IrList.getLastOp(), address);
                IrList.getInstance().addIr(storeIR);
                break;
            case Print:
                ArrayList<String> chars = parseFormatString();
                ArrayList<String> results = new ArrayList<>();
                for (Exp exp : exps) {
                    exp.translate();
                    results.add(IrList.getLastOp());
                }
                int argCount = 0;
                for (String character : chars) {
                    if (character == null) {
                        if (argCount < exps.size()) {
                            //exps.get(argCount).translate();
                            Generator.putInt(results.get(argCount));
                            argCount++;
                        }
                        else {
                            Error error = new Error("l", line);
                            ErrorList.getInstance().addError(error);
                        }
                    }
                    else {
                        Generator.putCh(character);
                    }
                }
                if (argCount != exps.size()) {
                    Error error = new Error("l", line);
                    ErrorList.getInstance().addError(error);
                }//exps is more than argCount
                break;
            case Condition:
                translateCondition();
                break;
            case Loop:
                int oldDepth = cycleDepth;
                cycleDepth++;
                translateLoop();
                if (cycleDepth != oldDepth) {
                    cycleDepth--;
                    //normal exit the loop
                }
                break;
            case Continue:
                String loopBegin = loopBeginStack.peek();
                IrList.getInstance().getNowBlock().setBr(new BrIR(loopBegin, 1));
                IrList.getInstance().newBlock();
                break;
            case Break:
                if (cycleDepth == 0) {
                    Error error = new Error("m", line);
                    ErrorList.getInstance().addError(error);
                }
                else {
                    ArrayList<BrIR> breaks = breakMap.get(cycleDepth);
                    BrIR breakIR = new BrIR();
                    IrList.getInstance().getNowBlock().setBr(breakIR);
                    breaks.add(breakIR);
                    IrList.getInstance().newBlock();
                }
                break;
            default:
                break;
        }
    }

    private void translateCondition() {
        IrList.getInstance().getNowBlock().setBr(new BrIR());
        BasicBlock turnToCond = IrList.getInstance().getNowBlock();
        cond.translate();
        turnToCond.getBr().setDest(cond.getLOrExp().getTurnToAnd().getLabel());
        if (stmts.size() == 1) {
            //if Body
            IrList.getInstance().newBlock();
            cond.setBody(IrList.getInstance().getNowBlock().getLabel());
            stmts.get(0).translate();
            //if End
            BrIR jumpToEnd = new BrIR();
            BasicBlock lastBlock = IrList.getInstance().getNowBlock();
            lastBlock.setBr(jumpToEnd);
            IrList.getInstance().newBlock();
            String nowLabel = IrList.getInstance().getNowBlock().getLabel();
            lastBlock.getBr().setDest(nowLabel);
            cond.setEnd(nowLabel);
        }
        else {
            //if Body
            IrList.getInstance().newBlock();
            cond.setBody(IrList.getInstance().getNowBlock().getLabel());
            stmts.get(0).translate();
            BrIR ifToEnd = new BrIR();
            BasicBlock IfBlock = IrList.getInstance().getNowBlock();
            IfBlock.setBr(ifToEnd);
            //else Body
            IrList.getInstance().newBlock();
            cond.setEnd(IrList.getInstance().getNowBlock().getLabel());
            stmts.get(1).translate();
            BrIR elseToEnd = new BrIR();
            BasicBlock ElseBlock = IrList.getInstance().getNowBlock();
            ElseBlock.setBr(elseToEnd);
            //End of if and else
            IrList.getInstance().newBlock();
            String nowLabel = IrList.getInstance().getNowBlock().getLabel();
            IfBlock.getBr().setDest(nowLabel);
            ElseBlock.getBr().setDest(nowLabel);
        }
    }

    private void translateLoop() {
        IrList.getInstance().getNowBlock().setBr(new BrIR());
        BasicBlock turnToCond = IrList.getInstance().getNowBlock();
        cond.translate();
        String loopBegin = cond.getLOrExp().getTurnToAnd().getLabel();
        turnToCond.getBr().setDest(loopBegin);
        loopBeginStack.push(loopBegin);
        ArrayList<BrIR> breaks = new ArrayList<>();
        breakMap.put(cycleDepth, breaks);
        // while body
        IrList.getInstance().newBlock();
        cond.setBody(IrList.getInstance().getNowBlock().getLabel());
        stmts.get(0).translate();
        loopBeginStack.pop();
        //jump to Cond
        BrIR whileBr  = new BrIR();
        whileBr.setDest(cond.getLOrExp().getTurnToAnd().getLabel());
        //loop begin may change here
        IrList.getInstance().getNowBlock().setBr(whileBr);
        //while end
        IrList.getInstance().newBlock();
        String whileEnd = IrList.getInstance().getNowBlock().getLabel();
        cond.setEnd(whileEnd);
        breaks = breakMap.get(cycleDepth);
        for (BrIR brIR : breaks) {
            brIR.setDest(whileEnd);
        }
    }
    private ArrayList<String> parseFormatString() {
        String s = formatString.getValue();
        ArrayList<String> chars = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            int nextChar = i + 1 < s.length() ? s.charAt(i+1) : -1;
            switch (s.charAt(i)) {
                case '%':
                    if (nextChar == 'd') {
                        i++;//pass d
                        chars.add(null);//replace here
                    }
                    else {
                        Error error = new Error("a", formatString.getLine());
                        ErrorList.getInstance().addError(error);
                        i++;//pass
                    }
                    break;
                case '\\':
                    if (nextChar == 'n') {
                        i++;
                        chars.add("10");
                    }
                    else {
                        Error error = new Error("a", formatString.getLine());
                        ErrorList.getInstance().addError(error);
                        i++;//pass
                    }
                    break;
                case '"':
                    break;
                default:
                    int n = s.charAt(i);
                    if (n == 32 || n == 33 || (n >= 40 && n <= 126)) {
                        chars.add(String.valueOf(n));
                    }
                    else {
                        Error error = new Error("a", formatString.getLine());
                        ErrorList.getInstance().addError(error);
                    }
                    break;
            }
        }
        return chars;
    }

    public int checkReturn() {
        if (type == StmtType.Return && !exps.isEmpty()) {
            return line;
        }
        return -1;
    }
}
