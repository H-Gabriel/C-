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
        boolean valid = declarationList();
        System.out.println(valid ? "VÁLIDO" : "INVÁLIDO");
    }

    private static boolean declarationList() {
        boolean valid = true;
        while (valid && index != tokens.size()) {
            valid = declaration();
        }
        return valid;
    }

    private static boolean declaration() {
        return varDeclaration() || funDeclaration();
    }

    private static boolean varDeclaration() {
        boolean hasSpecifier = typeSpecifier();
        boolean hasId = hasSpecifier && id();
        if (hasId && getToken().equals(";")) {
            index++;
            rollback = 0;
            return true;
        }
        if (hasId && getToken().equals("[")) {
            index++;
            rollback++;
            if (num() && getToken().equals("]")) {
                index++;
                rollback++;
                if (getToken().equals(";")) {
                    index++;
                    rollback = 0;
                    return true;
                }
            }
        }
        index -= rollback;
        rollback = 0;
        return false;
    }

    private static boolean typeSpecifier() {
        String token = getToken();
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
        if (hasId && getToken().equals("(")) {
            index++;
            if (params() && getToken().equals(")")) {
                index++;
                rollback = 0;
                return compoundStmt();
            }
        }
        return false;
    }

    private static boolean params() {
        if (getToken().equals(")")) {
            // Não incrementar o index.
            return true;
        }
        return paramList();
    }

    private static boolean paramList() {
        boolean hasParamList = param();
        while (getToken().equals(",")) {
            index++; // Consumo da virgula
            param();
        }
        return hasParamList;
    }

    private static boolean param() {
        boolean hasSpecifier = typeSpecifier();
        boolean hasId = hasSpecifier && id();
        if (hasId) {
            if (getToken().equals("[") && tokens.get(index + 1).getContent().equals("]")) {
                index += 2;
            }
            return true;
        }
        return false;
    }

    private static boolean compoundStmt() {
        if (getToken().equals("{")) {
            index++;
            if (localDeclarations() && statementList() && getToken().equals("}")) {
                index++;
                return true;
            }
        }
        return false;
    }

    private static boolean localDeclarations() {
        boolean hasVarDeclaration = varDeclaration();
        while (hasVarDeclaration) {
            hasVarDeclaration = varDeclaration();
        }
        return true;
    }

    private static boolean statementList() {
        boolean hasStatement = statement();
        while (hasStatement) {
            hasStatement = statement();
        }
        return true;
    }

    private static boolean statement() {
        return expressionStmt() || compoundStmt() || selectionStmt() || iterationStmt() || returnStmt();
    }

    private static boolean expressionStmt() {
        if (getToken().equals(";")) {
            index++;
            return true;
        }
        return expression() && getToken().equals(";");
    }

    private static boolean selectionStmt() {
        if (getToken().equals("if")) {
            index++;
            if (getToken().equals("(")) {
                index++;
                if (expression() && getToken().equals(")")) {
                    index++;
                    if (statement()) {
                        if (getToken().equals("else")) {
                            index++;
                            return statement();
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean iterationStmt() {
        if (getToken().equals("while")) {
            index++;
            if (getToken().equals("(")) {
                index++;
                if (expression() && getToken().equals(")")) {
                    index++;
                    return statement();
                }
            }
        }
        return false;
    }

    public static boolean returnStmt() {
        if (getToken().equals("return")) {
            index++;
            if (getToken().equals(";")) {
                index++;
                return true;
            }
            return expression() && getToken().equals(";");
        }
        return false;
    }

    private static boolean expression() {
        if (num()) return true;

        if (var()) {
            if (getLexem().equals(Lexems.SYMBOL)) {
                index++;
                return num();
            }
            if (getToken().equals("=")) {
                index++;
                return expression();
            }
            return simpleExpression();
        }
        return false;
    }

    private static boolean var() {
        if (id()) {
            if (getToken().equals("[")) {
                index++;
                if (expression() && getToken().equals("]")) {
                    index++;
                    return true;
                }
            }
            return true;
        }
        return false;
    }

    private static boolean simpleExpression() {
        if (additiveExpression()) {
            if (relop()) {
                return additiveExpression();
            }
            return true;
        }
        return false;
    }

    private static boolean relop() {
        if (getToken().equals("<=") || getToken().equals("<") || getToken().equals(">") ||
            getToken().equals(">=") || getToken().equals("==") || getToken().equals("!=")) {
            index++;
            return true;
        }
        return false;
    }

    private static boolean additiveExpression() {
        if (term()) {
            if (addop()) {
                if (term()) {
                    while (additiveExpression()) {
                        // Não fazer nada aqui, isso consome a recursão à esquerda da definição de additiveExpression
                    }
                }
            }
            return true;
        }
        return false;
    }

    private static boolean term() {
        if (factor()) {
            if (mulop()) {
                return factor();
            }
            return true;
        }
        return false;
    }

    private static boolean mulop() {
        if (getToken().equals("*") || getToken().equals("/")) {
            index++;
            return true;
        }
        return false;
    }

    private static boolean factor() {
        if (getToken().equals("(")) {
            index++;
            if (expression() && getToken().equals(")")) {
                index++;
                return true;
            }
        } else if (var()) {
            return true;
        } else if (call()) {
            return true;
        } else return num();
        return false;
    }

    private static boolean call() {
        if (id()) {
            if (getToken().equals("(")) {
                index++;
                if (args() && getToken().equals(")")) {
                    index++;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean args() {
        if (getToken().equals(")")) {
            return true;
        }
        return argList();
    }

    private static boolean argList() {
        if (expression()) {
            while (getToken().equals(",")) {
                index++;
                expression();
            }
            return true;
        }
        return false;
    }

    private static boolean addop() {
        if (getToken().equals("+") || getToken().equals("-")) {
            index++;
            return true;
        }
        return false;
    }

    private static boolean id() {
        if (getLexem().equals(Lexems.ID)) {
            index++;
            rollback++;
            return true;
        }
        return false;
    }

    private static boolean num() {
        if (getLexem().equals(Lexems.NUM)) {
            index++;
            rollback++;
            return true;
        }
        return false;
    }

    private static Lexems getLexem() {
        return tokens.get(index).getLexem();
    }

    private static String getToken() {
        return tokens.get(index).getContent();
    }
}
