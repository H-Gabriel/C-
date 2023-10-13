package syntax;

import enums.Lexems;
import lexer.Lexer;
import lexer.Token;

import java.util.List;

/*
	Esse é o parser do compilador. Ele é responsável por verificar se a entrada
	é válida ou não. Para isso, ele utiliza a lista de tokens gerada pelo lexer.
	A BNF da linguagem é a seguinte:
	checkmark character: ✓
	cross mark character: ✗
	✓ 1.    program -> declaration-list
	✓ 2.    declaration-list -> declaration-list declaration | declaration
	✓ 3.    declaration -> var-declaration | fun-declaration
	✓ 4.    var-declaration -> type-specifier ID ; | type-specifier ID [ NUM ] ;
	✓ 5.    type-specifier -> int | void
	✓ 6.    fun-declaration -> type-specifier ID ( params ) compound-stmt
	✓ 7.    params -> param-list | void
	✓ 8.    param-list -> param-list , param | param
	✓ 9.    param -> type-specifier ID | type-specifier ID [ ]
	✓ 10.   compound-stmt -> { local-declarations statement-list }
	✗ 11.   local-declarations -> local-declarations var-declarations | empty
	✗ 12.   statement-list -> statement-list statement | empty
	✗ 13.   statement -> expression-stmt | compound-stmt | selection-stmt | iteration-stmt | return-stmt
	✗ 14.   expression-stmt -> expression ; | ;
	✗ 15.   selection-stmt -> if ( expression ) statement | if ( expression ) statement else statement
	✗ 16.   iteration-stmt -> while ( expression ) statement
	✗ 17.   return-stmt -> return ; | return expression ;
	✗ 18.   expression -> var = expression | simple-expression
	✗ 19.   var -> ID | ID [ expression ]
	✗ 20.   simple-expression -> additive-expression relop additive-expression | additive-expression
	✗ 21.   relop -> <= | < | > | >= | == | !=
	✗ 22.   additive-expression -> additive-expression addop term | term
	✗ 23.   addop -> + | -
	✗ 24.   term -> term mulop factor | factor
	✗ 25.   mulop -> * | /
	✗ 26.   factor -> ( expression ) | var | call | NUM
	✗ 27.   call -> ID ( args )
	✗ 28.   args -> arg-list | empty
	✗ 29.   arg-list -> arg-list , expression | expression

	Keywords: else if int return void while
	Special symbols: + - * / < <= > >= == != = ; , ( ) [ ] { } /* *\/
	ID = letter letter *
	NUM = digit digit *
	letter = a | .. | z | A | .. | Z
	digit = 0 | .. | 9
	Comments: /* ... *\/
*/

public class Parser {

    private static List<Token> tokens;

    private static int index = 0;

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
        boolean hasSpecifier = typeSpecifier();
        boolean hasId = hasSpecifier && id();
        return hasId && (varDeclaration() || funDeclaration());
    }

    private static boolean varDeclaration() {
        if (getToken().equals(";")) {
            index++;
            return true;
        } else if (getToken().equals("[")) {
            index++;
            if (num() && getToken().equals("]")) {
                index++;
                if (getToken().equals(";")) {
                    index++;
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean funDeclaration() {
        if (getToken().equals("(")) {
            index++;
            if (params() && getToken().equals(")")) {
                index++;
                return compoundStmt();
            }
        }
        return false;
    }

    private static boolean typeSpecifier() {
        String token = getToken();
        if (token.equals("int") || token.equals("void")) {
            index++;
            return true;
        }
        return false;
    }

    private static boolean params() {
        if (getToken().equals(")")) {
            index++;
            return true;
        } else {
            return paramList();
        }
    }

    private static boolean paramList() {
        boolean hasParamList = param();
        while (getToken().equals(",")) {
            param();
        }
        return hasParamList;
    }

    private static boolean param() {
        boolean hasSpecifier = typeSpecifier();
        boolean hasId = hasSpecifier && id();
        if (hasId) {
            if (getToken().equals("[") && tokens.get(index + 1).getContent().equals("(")) {
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
        return false;
    }

    private static boolean statementList() {
        return false;
    }

    private static boolean id() {
        if (tokens.get(index).getLexem() == Lexems.ID) {
            index++;
            return true;
        }
        return false;
    }

    private static boolean num() {
        if (tokens.get(index).getLexem() == Lexems.NUM) {
            index++;
            return true;
        }
        return false;
    }

    private static String getToken() {
        return tokens.get(index).getContent();
    }
}
