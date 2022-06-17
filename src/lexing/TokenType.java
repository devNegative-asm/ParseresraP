package lexing;
import java.util.HashMap;

import parsing.ParsingState;

public class TokenType {
	private static HashMap<String, TokenType> tokenTypes = new HashMap<>();
	public final String name;
	public static TokenType fromName(String name) {
		TokenType result = tokenTypes.get(name);
		if(result==null) {
			result = new TokenType(name);
			tokenTypes.put(name, result);
		}
		return result;
	}
	private TokenType(String name) {
		this.name=name;
	}
	public String toString() {
		return name;
	}
	public static boolean isTokenType(String s) {
		return tokenTypes.containsKey(s);
	}
	public static HashMap<String, TokenType> getMappings() {
		return tokenTypes;
	}
	public static HashMap<String, TokenType> resetMappings() {
		HashMap<String, TokenType> old = tokenTypes;
		tokenTypes = new HashMap<>();
		return old;
	}
	public static HashMap<String, TokenType> swapMappings(HashMap<String, TokenType> newState) {
		HashMap<String, TokenType> old = tokenTypes;
		tokenTypes = newState;
		return old;
	}
}
