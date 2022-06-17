package parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import lexing.Token;

public class ParsingState {
	private static HashMap<String, ParsingState> parsingStates = new HashMap<>();
	public static HashMap<String, ParsingState> getMappings() {
		return parsingStates;
	}
	public static HashMap<String, ParsingState> resetMappings() {
		HashMap<String, ParsingState> old = parsingStates;
		parsingStates = new HashMap<>();
		return old;
	}
	public static HashMap<String, ParsingState> swapMappings(HashMap<String, ParsingState> newState) {
		HashMap<String, ParsingState> old = parsingStates;
		parsingStates = newState;
		return old;
	}
	public final String name;
	
	private ArrayList<ParsingRule> rules = new ArrayList<>();
	public static ParsingState fromName(String name) {
		ParsingState result = parsingStates.get(name);
		if(result==null) {
			result = new ParsingState(name);
			parsingStates.put(name, result);
		}
		return result;
	}
	private ParsingState(String name) {
		this.name=name;
	}
	public SyntaxTree parse(PausableProvider<Token> tokens) throws ParseFailureSignal {
		if(!tokens.hasNext()) {
			return null;
		}
		SyntaxTree origin = new SyntaxTree(this,tokens.peek());
		ArrayList<SyntaxTree> subtrees = null;
		Object checkpoint = tokens.getCheckpoint();
		for(ParsingRule rule:rules) {
			try {
				subtrees = rule.findChildren(tokens, origin, this);
				break;
			} catch(ParseFailureSignal e) {
				tokens.restoreCheckpoint(checkpoint);
			}
		}
		if(subtrees == null) {
			throw new ParseFailureSignal("no valid continuation found for parsing state ["+this+"] after token "+tokens.peek());
		} else {
			subtrees.forEach(subtree -> origin.addChild(subtree));
			return origin;
		}
	}
	public void addRule(ParsingRule rule) {
		rules.add(rule);
	}
	public String toString() {
		StringBuilder ruleString = new StringBuilder();
		rules.forEach(v -> ruleString.append("|").append(v.toString()).append('\n'));
		return ruleString.toString();
	}
	boolean helperState = false;
	public void makeHelperState() {
		helperState = true;
	}
}
