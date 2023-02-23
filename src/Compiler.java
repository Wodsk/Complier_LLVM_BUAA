import errors.ErrorList;
import generator.Generator;
import models.llvm.IrList;
import models.treemodels.leaf.Token;
import io.ReadFile;
import io.WriteFile;
import lexer.Lexer;
import models.treemodels.root.CompUnit;
import parser.Parser;

import java.io.IOException;
import java.util.ArrayList;

public class Compiler {

    public static void main(String[] args) throws IOException {
        Token token;
        ReadFile readFile = ReadFile.getInstance();
        WriteFile writeFile = WriteFile.getInstance();
        Lexer lexer = new Lexer();
        int n = readFile.readNotNull();
        ArrayList<Token> tokenList = new ArrayList<>();
        while (n != -1) {
            token = lexer.analyzeCharacter(n);
            if (token.isValid()) {
                tokenList.add(token);
            }
            n = token.getNextChar();
        }
        Token endToken = new Token("END", "-1", -1);
        tokenList.add(endToken);
        Parser parser = new Parser(tokenList);
        CompUnit compUnit = parser.parser();
        Generator generator = new Generator(compUnit);
        generator.generator();
        IrList.getInstance().print();
        //ErrorList.getInstance().print();
        writeFile.close();
    }

}
