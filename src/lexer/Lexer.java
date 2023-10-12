package lexer;

import enums.Lexems;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Lexer {
    private static final List<Token> tokens = new LinkedList<>();

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
                    tokens.add(new Token(Lexems.SYMBOL, "*/"));
                    comment = false;
                    commentText = "";
                    i++;
                    continue;
                } else if (j == str.length()) {
                    commentText = commentText.concat(token);
                    System.out.println("[INVALID] " + commentText);
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

                tokens.add(new Token(Lexems.NUM, token));
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
                    tokens.add(new Token(Lexems.KEYWORD, token));
                } else {
                    tokens.add(new Token(Lexems.ID, token));
                }
                continue;
            }

            if (LexerUtil.isStartSpecialSymbol(character)) {
                if (i + 1 == str.length()) {
                    if (token.equals("!")) {
                        System.out.println("[INVALID] " + token);
                    } else {
                        tokens.add(new Token(Lexems.SYMBOL, token));
                    }
                } else {
                    String symbol = token.concat(Character.toString(str.charAt(i + 1)));
                    if (LexerUtil.isFullSpecialSymbol(symbol)) {
                        tokens.add(new Token(Lexems.SYMBOL, symbol));
                        comment = symbol.equals("/*");
                        i++;
                    } else {
                        if (token.equals("!")) {
                            System.out.println("[INVALID]: " + token);
                        } else {
                            tokens.add(new Token(Lexems.SYMBOL, token));
                        }
                    }
                }
                continue;
            }

            if (LexerUtil.isSingleSpecialSymbol(character)) {
                tokens.add(new Token(Lexems.SYMBOL, token));
                continue;
            }

            if (!token.equals(" ")) {
                System.out.println("[INVALID] " + token);
            }
        }
    }

    public static List<Token> getTokens() {
        return tokens;
    }
}