package syntax;

import enums.Lexems;
import lexer.Lexer;
import lexer.Token;

import java.util.List;

public class Parser {
    private static List<Token> tokens;
    private static int index = 0;
    private static int rollback = 0;

    public static void main(String[] args) {
        Lexer.main(args);
        tokens = Lexer.getTokens();
        parse();
    }

    private static void parse() {
        declarationList();
    }

    public static void rollback() {
        index = index - rollback;
        rollback = 0;
    }

    private static void declarationList() {
        boolean valid = true;
        while (valid && index != tokens.size()) {
            valid = declaration();
        }

        if (!valid) {
            System.out.println("INVÁLIDO");
        }
    }

    private static boolean declaration() {
        if (varDeclaration()) {
            return true;
        } else {
            rollback();
            return funDeclaration();
        }
    }

    private static boolean varDeclaration() {
        boolean hasSpecifier = typeSpecifier();
        boolean hasId = hasSpecifier && id();
        if (hasId && tokens.get(index).getContent().equals(";")) {
            index++;
            rollback = 0;
            return true;
        } else if (hasId && tokens.get(index).getContent().equals("[")){
            index++;
            rollback++;
            if (num() && tokens.get(index).getContent().equals("]")) {
                index++;
                rollback++;
                if (tokens.get(index).getContent().equals(";")) {
                    index++;
                    rollback = 0;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean typeSpecifier() {
        String token = tokens.get(index).getContent();
        if (token.equals("int") || token.equals("void")) {
            index++;
            rollback++;
            return true;
        }
        return false;
    }

    private static boolean funDeclaration() {
        boolean hasSpecifier = typeSpecifier();
        boolean hasId = hasSpecifier && id();
        if (hasId && tokens.get(index).getContent().equals("(")) {
            index++;
            rollback++;
            if (params() && tokens.get(index).getContent().equals(")")) {
                index++;
                rollback++;
                return compoundStmt();
            }
        }
        return false;
    }

    private static boolean params() {
        if (tokens.get(index).getContent().equals(")")) {
            return true;
        } else {
            return paramList();
        }
    }

    private static boolean paramList() {
        boolean hasParamList = param();
        while (tokens.get(index).getContent().equals(",")) {
            param();
        }
        return hasParamList;
    }

    private static boolean param() {
        boolean hasSpecifier = typeSpecifier();
        boolean hasId = hasSpecifier && id();

        if (hasId) {
            if (tokens.get(index).getContent().equals("[")
                    && tokens.get(index + 1).getContent().equals("(")) {
                index += 2;
            }
            //TODO: PRECISA FAZER ALGO COM O ROLLBACK? PENSAR DEPOIS
            return true;
        }

        return false;
    }

    private static boolean compoundStmt() {
        //TODO: PARAMOS AQUI
        return false;
    }

    private static boolean id() {
        if (tokens.get(index).getLexem() == Lexems.ID) {
            index++;
            rollback++;
            return true;
        }
        return false;
    }

    private static boolean num() {
        if (tokens.get(index).getLexem() == Lexems.NUM) {
            index++;
            rollback++;
            return true;
        }
        return false;
    }
}
