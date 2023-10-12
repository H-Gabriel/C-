package lexer;

import enums.Lexems;

public class Token {
    private Lexems lexem;
    private String content;

    public Token(Lexems lexem, String content) {
        this.lexem = lexem;
        this.content = content;
    }

    public Lexems getLexem() {
        return lexem;
    }

    public String getContent() {
        return content;
    }
}
