package lexing;

public class LexingRule {
	private final String tokenOutputType;
	public final String pattern;
	public LexingRule(String tokenOutputType, String pattern) {
		this.tokenOutputType = tokenOutputType;
		this.pattern = pattern;
	}
	public boolean canMatch(String input) {
		return input.matches(pattern);
	}
	public TokenType getTokenType() {
		return TokenType.fromName(tokenOutputType);
	}
	public String toString() {
		return String.format("<LexingRule name=\"%s\" pattern=\"%s\" />",
				tokenOutputType,
				pattern);
	}
}
