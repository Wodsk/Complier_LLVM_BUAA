package models.encode;

public enum NCode {
    CompUnit, Decl, FuncDef, MainFuncDef, ConstDecl, VarDecl,
    BType, ConstDef, ConstExp, ConstInitVal, VarDef, InitVal,
    Exp, FuncType, FuncFParams, Block, FuncFParam, BlockItem,
    Stmt, LVal, Cond, AddExp, LOrExp, PrimaryExp, Number,
    UnaryExp, UnaryOp, MulExp, RelExp, EqExp, LAndExp, FuncRParams;

    @Override
    public String toString() {
        return "<" + super.toString() + ">";
    }
}
