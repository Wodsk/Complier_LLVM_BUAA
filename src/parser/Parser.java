package parser;

import errors.Error;
import errors.ErrorList;
import jdk.nashorn.internal.runtime.ParserException;
import models.encode.StmtType;
import models.treemodels.root.CompUnit;
import models.treemodels.leaf.Token;
import io.WriteFile;
import models.treemodels.root.block.Block;
import models.treemodels.root.block.BlockItem;
import models.treemodels.root.block.Stmt;
import models.treemodels.root.decl.Decl;
import models.treemodels.root.decl.constdecl.ConstDecl;
import models.treemodels.root.decl.constdecl.ConstDef;
import models.treemodels.root.decl.constdecl.ConstInitVal;
import models.treemodels.root.decl.vardecl.InitVal;
import models.treemodels.root.decl.vardecl.VarDecl;
import models.treemodels.root.decl.vardecl.VarDef;
import models.treemodels.root.exp.*;
import models.treemodels.root.exp.common.*;
import models.treemodels.root.exp.condition.Cond;
import models.treemodels.root.func.FuncRParams;
import models.treemodels.root.func.funcdef.*;
import symbol.SymbolTable;

import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    private final ArrayList<Token> tokenList;
    private int index;
    //index is one char faster than nowToken
    private Token nowToken;
    private SymbolTable currentTable = null;
    private final WriteFile writeFile = WriteFile.getInstance();

    public Parser(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
        this.index = 0;
    }

    public CompUnit parser() throws IOException {
        currentTable = new SymbolTable(null);
        //global reg address start form a
        nowToken = readToken();
        return compUnit();
    }

    //没匹配到终结符之前，index不应该向后推动
    private CompUnit compUnit() throws IOException {
        CompUnit compUnit = new CompUnit();
        while (isDecl()) {
            compUnit.addDecl(decl());
        }
        while (isFuncDef()) {
            compUnit.addFuncDef(funcDef());
        }
        compUnit.setMainFuncDef(mainFuncDef());
        compUnit.setSelfTable(currentTable);
        compUnit.printRoot();
        return compUnit;
    }

    private boolean isDecl() {
        if (nowToken.getSymbol().equals("CONSTTK")) {
            return true;
        } else if (nowToken.getSymbol().equals("INTTK")) {
            Token next1Token = readToken();
            Token next2Token = readToken();
            backToken(2);
            return next1Token.getSymbol().equals("IDENFR") &&
                    !next2Token.getSymbol().equals("LPARENT");
        }
        return false;
    }

    private boolean isFuncDef() {
        if (nowToken.getSymbol().equals("VOIDTK")) {
            return true;
        } else if (nowToken.getSymbol().equals("INTTK")) {
            Token next1Token = readToken();
            Token next2Token = readToken();
            backToken(2);
            return next1Token.getSymbol().equals("IDENFR") &&
                    next2Token.getSymbol().equals("LPARENT");
        }
        return false;
    }

    private Decl decl() throws IOException {
        if (nowToken.getSymbol().equals("CONSTTK")) {
            int line = nowToken.getLine();
            outputToken();
            Decl decl = new Decl(constDecl(), line);
            decl.setSelfTable(currentTable);
            return decl;
        }
        else if (nowToken.getSymbol().equals("INTTK")) {
            int line = nowToken.getLine();
            Decl decl = new Decl(varDecl(), line);
            decl.setSelfTable(currentTable);
            return decl;
        }
        //throw new ParserException(nowToken.getValue());
        return null;
    }
    //Const Decl Indent functions

    private ConstDecl constDecl() throws IOException {
        judgeNowToken("INTTK");
        int line = nowToken.getLine();
        outputToken();
        ConstDecl constDecl = new ConstDecl(constDef(), line);
        while (nowToken.getSymbol().equals("COMMA")) {
            outputToken();
            constDecl.addConstDef(constDef());
        }
        judgeNowToken("SEMICN");
        outputToken();
        constDecl.setSelfTable(currentTable);
        constDecl.printRoot();
        return constDecl;
    }


    private ConstDef constDef() throws IOException {
        judgeNowToken("IDENFR");
        ConstDef constDef = new ConstDef(nowToken, nowToken.getLine());
        outputToken();
        while (nowToken.getSymbol().equals("LBRACK")) {
            outputToken();
            constDef.addDimenValue(constExp());
            judgeNowToken("RBRACK");
            outputToken();
        }
        judgeNowToken("ASSIGN");
        outputToken();
        constDef.setConstInitVal(constInitVal());
        constDef.setSelfTable(currentTable);
        constDef.printRoot();
        return constDef;
    }

    public ConstInitVal constInitVal() throws IOException {
        ConstInitVal constInitVal;
        if (nowToken.getSymbol().equals("LBRACE")) {
            constInitVal = new ConstInitVal(nowToken.getLine());
            outputToken();
            if (nowToken.getSymbol().equals("RBRACE")) {
                outputToken();
                constInitVal.printRoot();
                return constInitVal;
            }
            //it should write in First collection
            constInitVal.addConstInitVal(constInitVal());
            while (nowToken.getSymbol().equals("COMMA")) {
                outputToken();
                constInitVal.addConstInitVal(constInitVal());
            }
            judgeNowToken("RBRACE");
            outputToken();
        } else {
            int line = nowToken.getLine();
            constInitVal = new ConstInitVal(constExp(), line);
        }
        constInitVal.setSelfTable(currentTable);
        constInitVal.printRoot();
        return constInitVal;
    }

    public ConstExp constExp()
            throws IOException, ParserException {
        int line = nowToken.getLine();
        ConstExp constExp = new ConstExp(addExp(), line);
        constExp.setSelfTable(currentTable);
        constExp.printRoot();
        return constExp;
    }

    //Variable Decl Indent functions
    private VarDecl varDecl()
            throws IOException, ParserException {
        judgeNowToken("INTTK");
        int line = nowToken.getLine();
        outputToken();
        VarDecl varDecl = new VarDecl(varDef(), line);
        while (nowToken.getSymbol().equals("COMMA")) {
            outputToken();
            varDecl.addVarDef(varDef());
        }
        judgeNowToken("SEMICN");
        outputToken();
        varDecl.setSelfTable(currentTable);
        varDecl.printRoot();
        return varDecl;
    }

    private VarDef varDef()
            throws IOException, ParserException {
        judgeNowToken("IDENFR");
        VarDef varDef = new VarDef(nowToken, nowToken.getLine());
        outputToken();
        while (nowToken.getSymbol().equals("LBRACK")) {
            outputToken();
            varDef.addDimenValue(constExp());
            judgeNowToken("RBRACK");
            outputToken();
        }
        if (nowToken.getSymbol().equals("ASSIGN")) {
            outputToken();
            if (nowToken.getSymbol().equals("GETINTTK")) {
                outputToken();
                judgeNowToken("LPARENT");
                outputToken();
                judgeNowToken("RPARENT");
                outputToken();
                varDef.setGetInt(true);
            }
            else {
                varDef.setInitVal(initVal());
            }
        }
        varDef.setSelfTable(currentTable);
        varDef.printRoot();
        return varDef;
    }

    private InitVal initVal()
            throws IOException, ParserException {
        InitVal initVal;
        if (nowToken.getSymbol().equals("LBRACE")) {
            initVal = new InitVal(nowToken.getLine());
            outputToken();
            initVal.addInitVal(initVal());
            while (nowToken.getSymbol().equals("COMMA")) {
                outputToken();
                initVal.addInitVal(initVal());
            }
            judgeNowToken("RBRACE");
            outputToken();
        } else {
            int line = nowToken.getLine();
            initVal = new InitVal(exp(), line);
        }
        initVal.setSelfTable(currentTable);
        initVal.printRoot();
        return initVal;
    }

    private FuncDef funcDef()
            throws IOException, ParserException {
        FuncType funcType;
        if (nowToken.getSymbol().equals("VOIDTK") ||
                nowToken.getSymbol().equals("INTTK")) {
            funcType = new FuncType(nowToken);
            outputToken();
            funcType.printRoot();
        } else
            throw new ParserException(nowToken.getValue());
        judgeNowToken("IDENFR");
        FuncDef funcDef = new FuncDef(funcType, nowToken, nowToken.getLine());
        funcDef.setSelfTable(currentTable);
        currentTable = new SymbolTable(currentTable);
        //enter function and generate son
        outputToken();
        judgeNowToken("LPARENT");
        outputToken();
        if (nowToken.getSymbol().equals("INTTK")) {
            //it is the first collection of funcFParams
            funcDef.setFuncFParams(funcFParams());
        }//next is params
        judgeNowToken("RPARENT");
        outputToken();
        funcDef.setBlock(block(true));
        //FParams Table level is equal to functions block
        currentTable = currentTable.getFatherTable();
        //exit function and back to father
        funcDef.printRoot();
        return funcDef;
    }

    private FuncFParams funcFParams()
            throws IOException, ParserException {
        int line = nowToken.getLine();
        FuncFParams funcFParams = new FuncFParams(funcFParam(), line);
        funcFParams.setSelfTable(currentTable);
        while (nowToken.getSymbol().equals("COMMA")) {
            outputToken();
            funcFParams.addFuncFParam(funcFParam());
        }
        funcFParams.printRoot();
        return funcFParams;
    }

    private FuncFParam funcFParam() throws IOException {
        int dimension = 1;
        judgeNowToken("INTTK");
        outputToken();
        judgeNowToken("IDENFR");
        FuncFParam funcFParam = new FuncFParam(nowToken, nowToken.getLine());
        funcFParam.setSelfTable(currentTable);
        outputToken();
        if (nowToken.getSymbol().equals("LBRACK")) {
            outputToken();
            judgeNowToken("RBRACK");
            outputToken();
            funcFParam.setDimension(dimension);
            while (nowToken.getSymbol().equals("LBRACK")) {
                outputToken();
                dimension++;
                funcFParam.setDimension(dimension, constExp());
                judgeNowToken("RBRACK");
                outputToken();
            }
        }
        funcFParam.printRoot();
        return funcFParam;
    }

    private Block block(boolean isFunction) throws IOException {
        judgeNowToken("LBRACE");
        outputToken();
        Block block = new Block();
        if (!isFunction)
            currentTable = new SymbolTable(currentTable);
        //if block is not function then block needs to get into a new Table
        //if block is function then block is equal to the FuncFParams level
        block.setSelfTable(currentTable);
        while (!(nowToken.getSymbol().equals("RBRACE") ||
                nowToken.getSymbol().equals("END"))) {
            block.addBlockItem(blockItem());
        }
        judgeNowToken("RBRACE");
        block.setLine(nowToken.getLine());
        outputToken();
        block.printRoot();
        if (!isFunction)
            currentTable = currentTable.getFatherTable();
        return block;
    }

    private BlockItem blockItem() throws IOException {
        BlockItem blockItem;
        int line = nowToken.getLine();
        if (nowToken.getSymbol().equals("CONSTTK") ||
                nowToken.getSymbol().equals("INTTK")) {
            blockItem = new BlockItem(decl(), line);
        } else {
            blockItem = new BlockItem(stmt(), line);
        }
        blockItem.setSelfTable(currentTable);
        return blockItem;
    }

    private Stmt stmt() throws IOException {
        Stmt stmt;
        int line = nowToken.getLine();
        switch (nowToken.getSymbol()) {
            case "IFTK":
                outputToken();
                return parseIf(line);
            case "BREAKTK":
                outputToken();
                parseSemicn();
                stmt = new Stmt(StmtType.Break, line);
                stmt.setSelfTable(currentTable);
                stmt.printRoot();
                return stmt;
            case "CONTINUETK":
                outputToken();
                parseSemicn();
                stmt = new Stmt(StmtType.Continue, line);
                stmt.setSelfTable(currentTable);
                stmt.printRoot();
                return stmt;
            case "RETURNTK":
                outputToken();
                if (judgeIsExp(nowToken))
                    stmt = new Stmt(exp(), true, line);
                else
                    stmt = new Stmt(StmtType.Return, line);
                parseSemicn();
                stmt.setSelfTable(currentTable);
                stmt.printRoot();
                return stmt;
            case "PRINTFTK":
                outputToken();
                return parsePrintf(line);
            case "WHILETK":
                outputToken();
                return parseWhile(line);
            case "LBRACE":
                stmt = new Stmt(block(false), line);
                stmt.setSelfTable(currentTable);
                stmt.printRoot();
                return stmt;
            default:
                if (isGetInt())
                    return parseGetInt(line);
                else if (isAssignment()) {
                    return parseAssignment(line);
                } else if (judgeIsExp(nowToken))
                    stmt = new Stmt(exp(), false, line);
                else
                    stmt = new Stmt(StmtType.Empty, line);
                parseSemicn();
                stmt.setSelfTable(currentTable);
                stmt.printRoot();
                return stmt;
        }
    }

    private Stmt parseIf(int line) throws IOException {
        judgeNowToken("LPARENT");
        outputToken();
        Cond cond = cond();
        judgeNowToken("RPARENT");
        outputToken();
        Stmt condition = stmt();
        Stmt stmt = new Stmt(cond, condition, true, line);
        if (nowToken.getSymbol().equals("ELSETK")) {
            outputToken();
            stmt.addStmt(stmt());
        }
        stmt.setSelfTable(currentTable);
        stmt.printRoot();
        return stmt;
    }

    private Stmt parsePrintf(int line) throws IOException {
        judgeNowToken("LPARENT");
        outputToken();
        judgeNowToken("STRCON");
        Stmt stmt = new Stmt(nowToken, line);
        outputToken();
        while (nowToken.getSymbol().equals("COMMA")) {
            outputToken();
            stmt.addExp(exp());
        }
        judgeNowToken("RPARENT");
        outputToken();
        parseSemicn();
        stmt.setSelfTable(currentTable);
        stmt.printRoot();
        return stmt;
    }

    private Stmt parseWhile(int line) throws IOException {
        judgeNowToken("LPARENT");
        outputToken();
        Cond cond = cond();
        judgeNowToken("RPARENT");
        outputToken();
        Stmt stmt = new Stmt(cond, stmt(), false, line);
        stmt.setSelfTable(currentTable);
        stmt.printRoot();
        return stmt;
    }

    private boolean isGetInt() {
        if (nowToken.getSymbol().equals("IDENFR")) {
            for (int i = 0; index + i < tokenList.size(); i++) {
                Token token = tokenList.get(index + i);
                if (token.getSymbol().equals("ASSIGN")
                        && token.getLine() == nowToken.getLine()) {
                    Token nextToken = tokenList.get(index + i + 1);
                    return nextToken.getSymbol().equals("GETINTTK");
                } else if (token.getSymbol().equals("SEMICN")) {
                    return false;
                }
            }
        }
        return false;
    }

    private Stmt parseGetInt(int line) throws IOException {
        Stmt stmt = new Stmt(lVal(), line);
        judgeNowToken("ASSIGN");
        outputToken();
        judgeNowToken("GETINTTK");
        outputToken();
        judgeNowToken("LPARENT");
        outputToken();
        judgeNowToken("RPARENT");
        outputToken();
        parseSemicn();
        stmt.setSelfTable(currentTable);
        stmt.printRoot();
        return stmt;
    }

    private boolean isAssignment() {
        if (nowToken.getSymbol().equals("IDENFR")) {
            for (int i = 0; index + i < tokenList.size(); i++) {
                Token token = tokenList.get(index + i);
                if (token.getSymbol().equals("ASSIGN")
                        && token.getLine() == nowToken.getLine()) {
                    Token nextToken = tokenList.get(index + i + 1);
                    return !nextToken.getSymbol().equals("GETINTTK");
                } else if (token.getSymbol().equals("SEMICN")) {
                    return false;
                }
            }
        }
        return false;
    }

    private Stmt parseAssignment(int line) throws IOException {
        LVal lVal = lVal();
        judgeNowToken("ASSIGN");
        outputToken();
        Stmt stmt = new Stmt(lVal, exp(), line);
        parseSemicn();
        stmt.setSelfTable(currentTable);
        stmt.printRoot();
        return stmt;
    }

    private void parseSemicn() throws IOException {
        judgeNowToken("SEMICN");
        outputToken();
    }

    private Cond cond() throws IOException {
        Cond cond = new Cond(lorExp(), nowToken.getLine());
        cond.setSelfTable(currentTable);
        cond.printRoot();
        return cond;
    }

    private MainFuncDef mainFuncDef() throws IOException {
        judgeNowToken("INTTK");
        outputToken();
        judgeNowToken("MAINTK");
        outputToken();
        judgeNowToken("LPARENT");
        outputToken();
        judgeNowToken("RPARENT");
        outputToken();
        int line = nowToken.getLine();
        MainFuncDef mainFuncDef = new MainFuncDef(block(false), line);
        mainFuncDef.setSelfTable(currentTable);
        mainFuncDef.printRoot();
        return mainFuncDef;
    }

    private LVal lVal() throws IOException {
        judgeNowToken("IDENFR");
        LVal lVal = new LVal(nowToken, nowToken.getLine());
        outputToken();
        while (nowToken.getSymbol().equals("LBRACK")) {
            outputToken();
            lVal.addExp(exp());
            judgeNowToken("RBRACK");
            outputToken();
        }
        lVal.setSelfTable(currentTable);
        lVal.printRoot();
        return lVal;
    }

    //Exps
    private PrimaryExp primaryExp() throws IOException {
        PrimaryExp primaryExp;
        int line = nowToken.getLine();
        switch (nowToken.getSymbol()) {
            case "LPARENT":
                outputToken();
                primaryExp = new PrimaryExp(exp(), line);
                judgeNowToken("RPARENT");
                outputToken();
                primaryExp.setSelfTable(currentTable);
                primaryExp.printRoot();
                break;
            case "INTCON":
                primaryExp = new PrimaryExp(nowToken, line);
                outputToken();
                //writeFile.outputString("<Number>");
                primaryExp.setSelfTable(currentTable);
                primaryExp.printRoot();
                break;
            case "IDENFR":
                primaryExp = new PrimaryExp(lVal(), line);
                primaryExp.setSelfTable(currentTable);
                primaryExp.printRoot();
                break;
            default:
                System.out.println(nowToken.getSymbol());
                throw new ParserException(nowToken.getValue());
        }
        return primaryExp;
    }

    private UnaryExp unaryExp() throws IOException {
        UnaryExp unaryExp;
        if (nowToken.getSymbol().equals("PLUS") ||
                nowToken.getSymbol().equals("MINU") ||
                nowToken.getSymbol().equals("NOT")) {
            Token option = nowToken;
            outputToken();
            //writeFile.outputString("<UnaryOp>");
            unaryExp = new UnaryExp(option, unaryExp(), option.getLine());
        } else if (nowToken.getSymbol().equals("IDENFR")) {
            Token nextToken = readToken();
            if (nextToken.getSymbol().equals("LPARENT")) {
                unaryExp = new UnaryExp(nowToken, nowToken.getLine());
                //writeFile.outputToken(nowToken);
                nowToken = nextToken;//print Ident
                outputToken();//print LPARENT
                if (judgeIsExp(nowToken)) {
                    unaryExp.setFuncRParams(funcRParams());
                }
                judgeNowToken("RPARENT");
                outputToken();//print RPARENT
            } else {
                backToken(1);
                int line = nowToken.getLine();
                unaryExp = new UnaryExp(primaryExp(), line);
            }
        } else {
            int line = nowToken.getLine();
            unaryExp = new UnaryExp(primaryExp(), line);
        }
        unaryExp.setSelfTable(currentTable);
        unaryExp.printRoot();
        return unaryExp;
    }

    private FuncRParams funcRParams() throws IOException {
        int line = nowToken.getLine();
        FuncRParams funcRParams = new FuncRParams(exp(), line);
        while (nowToken.getSymbol().equals("COMMA")) {
            outputToken();
            funcRParams.addExp(exp());
        }
        funcRParams.setSelfTable(currentTable);
        funcRParams.printRoot();
        return funcRParams;
    }

    private MulExp mulExp() throws IOException {
        int line = nowToken.getLine();
        MulExp mulExp = new MulExp(unaryExp(), line);
        while (nowToken.getSymbol().equals("MULT") ||
                nowToken.getSymbol().equals("DIV") ||
                nowToken.getSymbol().equals("MOD") ||
                nowToken.getSymbol().equals("BITAND")) {
            mulExp.printRoot();
            Token option = nowToken;
            outputToken();
            mulExp.addUnaryExp(unaryExp(), option);
        }
        mulExp.setSelfTable(currentTable);
        mulExp.printRoot();
        return mulExp;
    }

    private AddExp addExp() throws IOException {
        int line = nowToken.getLine();
        AddExp addExp = new AddExp(mulExp(), line);
        while (nowToken.getSymbol().equals("PLUS") ||
                nowToken.getSymbol().equals("MINU")) {
            addExp.printRoot();
            Token option = nowToken;
            outputToken();
            addExp.addMulExp(mulExp(), option);
        }
        addExp.setSelfTable(currentTable);
        addExp.printRoot();
        return addExp;
    }

    private RelExp relExp() throws IOException {
        int line = nowToken.getLine();
        RelExp relExp = new RelExp(addExp(), line);
        while (nowToken.getSymbol().equals("LSS") ||
                nowToken.getSymbol().equals("LEQ") ||
                nowToken.getSymbol().equals("GRE") ||
                nowToken.getSymbol().equals("GEQ")) {
            relExp.printRoot();
            Token option = nowToken;
            outputToken();
            relExp.addAddExp(addExp(), option);
        }
        relExp.setSelfTable(currentTable);
        relExp.printRoot();
        return relExp;
    }

    private EqExp eqExp() throws IOException {
        int line = nowToken.getLine();
        EqExp eqExp = new EqExp(relExp(), line);
        while (nowToken.getSymbol().equals("EQL") ||
                nowToken.getSymbol().equals("NEQ")) {
            eqExp.printRoot();
            Token option = nowToken;
            outputToken();
            eqExp.addRelExp(relExp(), option);
        }
        eqExp.setSelfTable(currentTable);
        eqExp.printRoot();
        return eqExp;
    }

    private LAndExp landExp() throws IOException {
        int line = nowToken.getLine();
        LAndExp lAndExp = new LAndExp(eqExp(), line);
        while (nowToken.getSymbol().equals("AND")) {
            lAndExp.printRoot();
            Token option = nowToken;
            outputToken();
            lAndExp.addEqExp(eqExp(), option);
        }
        lAndExp.setSelfTable(currentTable);
        lAndExp.printRoot();
        return lAndExp;
    }

    private LOrExp lorExp() throws IOException {
        int line = nowToken.getLine();
        LOrExp lOrExp = new LOrExp(landExp(), line);
        while (nowToken.getSymbol().equals("OR")) {
            lOrExp.printRoot();
            Token option = nowToken;
            outputToken();
            lOrExp.addLAndExp(landExp(), option);
        }
        lOrExp.setSelfTable(currentTable);
        lOrExp.printRoot();
        return lOrExp;
    }

    private Exp exp() throws IOException {
        int line = nowToken.getLine();
        Exp exp = new Exp(addExp(), line);
        exp.setSelfTable(currentTable);
        exp.printRoot();
        return exp;
    }

    //IO
    public Token readToken() {
        Token token = tokenList.get(index);
        if (!token.getValue().equals("-1")) {
            index++;
        }
        return token;
    }

    public void backToken(int n) {
        index = index - n;
    }

    private void outputToken() throws IOException {
        //writeFile.outputToken(nowToken);
        nowToken = readToken();
        //output and read
    }

    private boolean judgeIsExp(Token token) {
        switch (token.getSymbol()) {
            case "IDENFR":
            case "LPARENT":
            case "INTCON":
            case "PLUS":
            case "NOT":
            case "MINU":
                return true;
            default:
                return false;
        }
    }

    private void judgeNowToken(String string) throws ParserException {
        int line;
        if (nowToken.getValue().equals("-1") || index == 1) {
            line = tokenList.get(index - 1).getLine();
        } else {
            line = tokenList.get(index - 2).getLine();
        }
        //index - 1 is the index of nowToken, because read Token will set index + 1
        if (!nowToken.getSymbol().equals(string)) {
            Error error = null;
            switch (string) {
                case "RPARENT":
                    error = new Error("j", line);
                    backToken(1);
                    break;
                case "RBRACK":
                    error = new Error("k", line);
                    backToken(1);
                    break;
                case "SEMICN":
                    error = new Error("i", line);
                    backToken(1);
                    //simulate read the character
                    break;
                default:
                    throw new ParserException(String.valueOf(nowToken.getLine()));
            }
            ErrorList.getInstance().addError(error);
        }
    }
}
