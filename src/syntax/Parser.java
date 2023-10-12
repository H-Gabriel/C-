package syntax;

import lexer.Lexer;
import lexer.Token;

import java.util.List;

public class Parser {
    public static void main(String[] args) {
        Lexer.main(args);
        List<Token> tokens = Lexer.getTokens();
        System.out.println(tokens);
    }
}
