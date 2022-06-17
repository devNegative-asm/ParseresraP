package parsing;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

import compiler.OilCompilationException;
import lexing.Token;

public class Parser {
	private ParsingState firstState = null;
	public Parser(Scanner sc) {
		String workingOnState = null;
		ParsingState curState = null;
		while(sc.hasNextLine()) {
			String line = sc.nextLine().trim();
			if(line.startsWith("#")||line.startsWith("//")||line.startsWith(";"))
				continue;
			if(line.isEmpty())
				continue;
			if(line.equals("Parse."))
				continue;
			if(line.matches("[A-Z][a-zA-Z]*\\s*\\??=")) {
				workingOnState = line.split("\\s*\\??=")[0];
					
				curState = ParsingState.fromName(workingOnState);
				if(firstState==null) {
					firstState = curState;
				}
				if(line.contains("?="))
					curState.makeHelperState();
			} else //Match Body ScopeOpen Phrase... ScopeClose -> (1 3)
			if(line.matches("([A-Z][A-Za-z]*(\\.{3})?\\s+)+->\\s+\\((\\s*|\\d+|\\d+(\\s\\d+)+)\\)")) {
				String[] re = line.split("->");
				String[] elements = re[0].trim().split("\\s+");
				String readingIndices = re[1].replaceAll("[)(]", "").trim();
				ArrayList<ParsingElement> subElements = new ArrayList<>();
				ArrayList<Integer> indices = new ArrayList<>();
				if(!readingIndices.isEmpty()) {
					for(String id:readingIndices.split("\\s")) {
						indices.add(Integer.parseInt(id));
					}
				}
				for(String element:elements) {
					
					ParsingElement elem;
					String convenientName = element.replace("...", "");
					elem = new ParsingElement(convenientName, element.endsWith("..."));
					subElements.add(elem);
				}
				ParsingRule rule = new ParsingRule(subElements, indices);
				curState.addRule(rule);
			} else {
				throw new OilCompilationException("error in grammar file: \""+line+"\" did not meet expected syntax");
			}
		}
	}
	public SyntaxTree parse(List<Token> tokens, Iterable<Consumer<PausableProvider<Token>>> tokenPreProcessors) {
		PausableProvider<Token> tokenProvider = new ListScanner<Token>(tokens);
		for(Consumer<PausableProvider<Token>> preprocessor:tokenPreProcessors) {
			Object checkpoint = tokenProvider.getCheckpoint();
			preprocessor.accept(tokenProvider);
			tokenProvider.restoreCheckpoint(checkpoint);
		}
		try {
			SyntaxTree result = this.firstState.parse(tokenProvider);
			if(tokenProvider.hasNext()) {
				throw new OilCompilationException("failed to parse starting with "+ tokenProvider.getExtent());
			}
			return result.collapse();
		} catch (ParseFailureSignal e) {
			throw new OilCompilationException(e.getMessage());
		}
	}
}
