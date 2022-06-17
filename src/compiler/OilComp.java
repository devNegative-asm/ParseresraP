package compiler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import lexing.Lexer;
import lexing.Token;
import parsing.Parser;
import parsing.ParsingState;

public class OilComp {
	public static void main(String[] args) throws Exception {
		FileInputStream fis = new FileInputStream(new File("grammar.txt"));
		Scanner grammarScanner = new Scanner(new BufferedInputStream(fis));
		Lexer lexer = new Lexer(grammarScanner);
		ArrayList<Token> tokens = lexer.lex(new File("src.oil"));
		Parser parser = new Parser(grammarScanner);
		System.out.println(parser.parse(tokens, Arrays.asList()).collapse().toXML(3,false));
	}
}