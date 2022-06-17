package parsing;

import static enums.ParsingElementType.*;
import enums.ParsingElementType;
import lexing.Token;
import lexing.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class ParsingElement {
	
	private TokenType expectedToken = null;
	ParsingState expectedState = null;
	private Function<PausableProvider<Token>,SyntaxTree> parserFunction = null;
	private final ParsingElementType type;
	private final boolean repeated;
	public String name() {
		switch(this.type) {
		case CUSTOM:
			return "<custom parsing element>";
		case STATE:
			return expectedState.name;
		case TOKEN:
			return expectedToken.name;
		default:
			return "<failure>";
		
		}
	}
	public String toString() {
		switch(this.type) {
		case CUSTOM:
			return "<custom parsing element>";
		case STATE:
			return "<STATE "+expectedState.name+">";
		case TOKEN:
			return "<TOKEN "+expectedToken.name+">";
		default:
			return "<failure>";
		
		}
	}
	public ParsingElement(String id, boolean repeated) {
		if(TokenType.isTokenType(id)) {
			type = TOKEN;
			expectedToken = TokenType.fromName(id);
		} else {
			type = STATE;
			expectedState = ParsingState.fromName(id);
		}
		this.repeated = repeated;
	}
	public ParsingElement(Function<PausableProvider<Token>,SyntaxTree> parserFunction) {
		this.parserFunction = parserFunction;
		type = CUSTOM;
		this.repeated = false;
	}
	public List<SyntaxTree> parse(PausableProvider<Token> toks) throws ParseFailureSignal {
		if(repeated) {
			ArrayList<SyntaxTree> trees = new ArrayList<>();
			Object checkpoint = toks.getCheckpoint();
			try {
				while(toks.hasNext()) {
					checkpoint = toks.getCheckpoint();
					trees.add(parseSingle(toks));
				}
			} catch(ParseFailureSignal e) {
				toks.restoreCheckpoint(checkpoint);
			}
			return trees;
		} else {
			return Arrays.asList(parseSingle(toks));
		}
	}
	private SyntaxTree parseSingle(PausableProvider<Token> toks) throws ParseFailureSignal {
		switch(type) {
			case TOKEN:
				Token t = toks.next();
				if(expectedToken!=t.type) {
					throw new ParseFailureSignal();
				}
				return new SyntaxTree(this,t);
			case STATE:
				return expectedState.parse(toks);
			case CUSTOM:
				return parserFunction.apply(toks);
		}
		return null;
	}
}
