package lexing;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import compiler.OilCompilationException;

import java.util.Scanner;

public class Lexer {
	
	private ArrayList<LexingRule> lexingRules = new ArrayList<>();
	private HashMap<String, String> recursivePatterns = new HashMap<>();
	public Lexer(Scanner fileInput) {
		while(fileInput.hasNextLine()) {
			String line = fileInput.nextLine().trim();
			if(line.startsWith("#")||line.startsWith("//")||line.startsWith(";"))
				continue;
			if(line.isEmpty())
				continue;
			if(line.equals("Parse."))
				break;
			
			String ruleName = line.split("=")[0].trim();
			boolean isRecursiveRule = !Character.isUpperCase(ruleName.charAt(0));
			String pattern = parseRegex(line.substring(line.indexOf('/')+1,line.lastIndexOf('/')));
			if(isRecursiveRule) {
				recursivePatterns.put(ruleName, pattern);
			} else {
				lexingRules.add(new LexingRule(ruleName, pattern));
			}
		}
	}
	private String parseRegex(String input) {
		for(Entry<String,String> rule:recursivePatterns.entrySet()) {
			input=input.replace("{"+rule.getKey()+"}", rule.getValue());
		}
		return "("+input+")";
	}
	
	private LexingRule runTokenTest(StringBuilder cs) {
		String tokString = cs.toString();
		for(LexingRule rule:lexingRules) {
			if(rule.canMatch(tokString))
				return rule;
		}
		return null;
		
	}
	
	public ArrayList<Token> lex(File source) {
		BufferedInputStream code = null;
		try {
			code = new BufferedInputStream(new FileInputStream(source));
			ArrayList<Token> tokens = new ArrayList<>();
			int line = 1;
			int characterInLine = 1;
			int startingLine = line;
			int startingCharacter = characterInLine;
			StringBuilder nextToken = new StringBuilder();
			LexingRule rule = null;
			while(code.available()>0) {
				code.mark(2);
				rule = runTokenTest(nextToken);
				int nextCodePoint = code.read();
				if(nextCodePoint=='\r')
					continue;
				nextToken.append((char)Byte.toUnsignedInt((byte)nextCodePoint));
				LexingRule nextRule = runTokenTest(nextToken);
				if(rule!=null && nextRule==null) {
					code.reset();
					tokens.add(new Token(rule.getTokenType(),nextToken.substring(0, nextToken.length()-1), source, startingLine, startingCharacter));
					nextToken = new StringBuilder();
					rule=null;
					startingLine = line;
					startingCharacter = characterInLine;
				} else {
					if(nextCodePoint=='\n') {
						line++;
						characterInLine = 1;
					} else {
						characterInLine++;
					}
					rule = nextRule;
				}
			}
			if(rule==null) {
				throw new OilCompilationException(
						"Parsing error in "+source+" line "+startingLine+" character "
						+startingCharacter+": "+nextToken.substring(0,Math.min(nextToken.length(), 20)).replace('\n',' '));
			} else {
				tokens.add(new Token(rule.getTokenType(),nextToken.toString(), source, line, characterInLine));
			}
			tokens.removeIf(t -> t.data.matches("\\s*"));
			return tokens;
		} catch (IOException e) {
			OilCompilationException error = new OilCompilationException("IO Failed when lexing input.");
			error.initCause(e);
			throw error;
		} finally {
			if(code!=null) {
				try {
					code.close();
				} catch (IOException e) {
					//what even
				}
			}
		}
	}
}
