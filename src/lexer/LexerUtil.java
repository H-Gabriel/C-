package lexer;

public class LexerUtil {
    public static boolean isNumeric(char character) {
        return character >= 48 && character <= 57;
    }

    public static boolean isLetter(char character) {
        return (character >= 65 && character <= 90) || (character >= 97 && character <= 122);
    }

    public static boolean isSingleSpecialSymbol(char character) {
        return character == '+' || character == '-' || character == '}'
                || character == '{' || character == ';' || character == ','
                || character == '(' || character == ')' || character == '['
                || character == ']';
    }

    public static boolean isStartSpecialSymbol(char character) {
        return character == '*' || character == '/' || character == '<'
                || character == '>' || character == '=' || character == '!';
    }

    public static boolean isFullSpecialSymbol(String str) {
        return str.equals("*/") || str.equals("/*") || str.equals("<=")
                || str.equals(">=") || str.equals("==") || str.equals("!=");
    }

    public static boolean isKeyword(String str) {
        return str.equals("else") || str.equals("if") || str.equals("int")
                || str.equals("return") || str.equals("void") || str.equals("while");
    }
}
