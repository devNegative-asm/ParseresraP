package parsing;

import java.util.ArrayList;
import java.util.List;

import lexing.Token;

public class ParsingRule {
	private List<ParsingElement> elements = new ArrayList<>();
	public List<Integer> takingIndices = new ArrayList<>();
	public ParsingRule(List<ParsingElement> elements, List<Integer> takingIndices) {
		this.elements.addAll(elements);
		this.takingIndices.addAll(takingIndices);
	}
	public ArrayList<SyntaxTree> findChildren(PausableProvider<Token> tokens, SyntaxTree tree, ParsingState parent) throws ParseFailureSignal{
		int index = 0;
		ArrayList<SyntaxTree> result = new ArrayList<>();
		for(ParsingElement elem:elements) {
			List<SyntaxTree> parseDeeper = elem.parse(tokens);
			if(takingIndices.contains(index)) {
				result.addAll(parseDeeper);
			}
			index++;
		}
		return result;
	}
	public String toString() {
		StringBuilder ruleString = new StringBuilder();
		elements.forEach(v -> ruleString.append(' ').append(v.toString()));
		return ruleString.toString();
	}
}
