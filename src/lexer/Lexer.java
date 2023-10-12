package lexer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Lexer {
    private static final List<String> tokens = new LinkedList<>();

    private static boolean comment = false;
    private static String commentText = "";

    public static void main(String[] args) {
        Scanner input = getFileScanner();
        if (input == null) {
            System.exit(1);
        }

        String text = input.nextLine();
        while (input.hasNextLine()) {
            String line = input.nextLine();
            text = text.concat(" ").concat(line);
        }
        text = text.replaceAll("\\s+", " ");
        analyze(text);

        input.close();
    }

    private static Scanner getFileScanner() {
        File file = new File("files/test.txt");
        try {
            return new Scanner(file);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + file.getAbsolutePath());
        }
        return null;
    }

    private static void analyze(String str) {
        for (int i = 0; i < str.length(); i++) {
            char character = str.charAt(i);
            String token = Character.toString(character);

            if (comment) {
                int j = i + 1;
                if (token.equals("*") && j < str.length() &&
                        token.concat(Character.toString(str.charAt(j))).equals("*/")) {
                    printToken("COMMENT", commentText, false);
                    printToken("SYMBOL", "*/", false);
                    comment = false;
                    commentText = "";
                    i++;
                    continue;
                } else if (j == str.length()) {
                    commentText = commentText.concat(token);
                    printToken(null, commentText, true);
                }
                commentText = commentText.concat(token);
                continue;
            }

            if (LexerUtil.isNumeric(character)) {
                int temp = i + 1;
                while (temp < str.length()) {
                    character = str.charAt(temp);
                    if (!LexerUtil.isNumeric(character)) {
                        break;
                    }
                    token = token.concat(Character.toString(character));
                    temp++;
                }
                i = temp - 1;

                printToken("NUM", token, false);
                continue;
            }

            if (LexerUtil.isLetter(character)) {
                int temp = i + 1;
                while (temp < str.length()) {
                    character = str.charAt(temp);
                    if (!LexerUtil.isLetter(character)) {
                        break;
                    }
                    token = token.concat(Character.toString(character));
                    temp++;
                }
                i = temp - 1;

                if (LexerUtil.isKeyword(token)) {
                    printToken("KEYWORD", token, false);
                } else {
                    printToken("ID", token, false);
                }
                continue;
            }

            if (LexerUtil.isStartSpecialSymbol(character)) {
                if (i + 1 == str.length()) {
                    printToken("SYMBOL", token, token.equals("!"));
                } else {
                    String symbol = token.concat(Character.toString(str.charAt(i + 1)));
                    if (LexerUtil.isFullSpecialSymbol(symbol)) {
                        printToken("SYMBOL", symbol, false);
                        comment = symbol.equals("/*");
                        i++;
                    } else {
                        printToken("SYMBOL", token, token.equals("!"));
                    }
                }
                continue;
            }

            if (LexerUtil.isSingleSpecialSymbol(character)) {
                printToken("SYMBOL", token, false);
                continue;
            }

            if (!token.equals(" ")) {
                printToken(null, token, true);
            }
        }
    }

    private static void printToken(String lexeme, String token, boolean err) {
        if (!err) {
            System.out.printf("[%s] %s\n", lexeme, token);
            tokens.add(token);
        } else {
            System.out.printf("[INVALID] %s\n", token);
        }
    }
}