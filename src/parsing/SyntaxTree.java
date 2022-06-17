package parsing;
import java.util.ArrayList;
import java.util.Arrays;
import lexing.Token;

public class SyntaxTree {
	Token sourceToken;
	ParsingElement originalElement = null;
	ParsingState originalState = null;
	private String id() {
		if(originalElement == null)
			return originalState.name;
		return originalElement.name();
	}
	ArrayList<SyntaxTree> children = new ArrayList<>();
	public SyntaxTree(ParsingElement originalElement, Token t) {
		sourceToken = t;
		this.originalElement = originalElement;
	}
	public SyntaxTree(ParsingState originalElement, Token t) {
		sourceToken = t;
		this.originalState = originalElement;
	}
	public void addChild(SyntaxTree result) {
		children.add(result);
	}
	public String toString() {
		return toJSON(2,true);
	}
	private String toIndentedJSON(int indentation, final int stride, boolean includeRedundantTokens) {
		StringBuilder childrenBuilder = new StringBuilder();
		int nextIndentationSpaces = indentation + stride;
		byte[] currentIndent = new byte[indentation];
		byte[] nextIndent = new byte[nextIndentationSpaces];
		Arrays.fill(currentIndent, (byte)' ');
		Arrays.fill(nextIndent, (byte)' ');
		String currentIndentation = new String(currentIndent).intern();
		String nextIndentation = new String(nextIndent).intern();
		
		boolean includeToken = includeRedundantTokens;
		if(children.isEmpty()) {
			
			String nextResult = currentIndentation + "{\n"
					+                nextIndentation + "\"type\": \""+id()+"\",\n"
					+                nextIndentation + "\"token\": \""+jsEscape(sourceToken.data)+"\"\n"
					+           currentIndentation + "}";
			
			
			return nextResult;
		} else if(children.size()==1) {
			childrenBuilder.append(children.get(0).toIndentedJSON(nextIndentationSpaces+stride,stride,includeRedundantTokens));
			includeToken |= children.get(0).sourceToken!=this.sourceToken;
		} else {
			childrenBuilder.append(children.get(0).toIndentedJSON(nextIndentationSpaces+stride,stride,includeRedundantTokens));
			includeToken |= children.get(0).sourceToken!=this.sourceToken;
			for(int i=1;i<children.size();i++) {
				childrenBuilder.append(",\n").append(children.get(i).toIndentedJSON(nextIndentationSpaces+stride,stride,includeRedundantTokens));
			}
		}
		
		
		String result =  currentIndentation + "{\n"
				+                nextIndentation + "\"type\": \""+id()+"\",\n"
				+                (includeToken? nextIndentation + "\"token\": \""+jsEscape(sourceToken.data)+"\"\n" :"")
				+                nextIndentation + "\"children\": [\n"
				+                    childrenBuilder.toString()+"\n"+nextIndentation+"]\n"
				+           currentIndentation + "}";
		return result;
	}
	public String toJSON(int indentation, boolean includeRedundantTokens) {
		return toIndentedJSON(0,indentation, includeRedundantTokens);
	}
	private String toIndentedXML(int indentation, final int stride, boolean includeRedundantTokens) {
		StringBuilder childrenBuilder = new StringBuilder();
		
		int nextIndentationSpaces = indentation + stride;
		byte[] currentIndent = new byte[indentation];
		byte[] nextIndent = new byte[nextIndentationSpaces];
		Arrays.fill(currentIndent, (byte)' ');
		Arrays.fill(nextIndent, (byte)' ');
		String currentIndentation = new String(currentIndent).intern();
		String nextIndentation = new String(nextIndent).intern();

		boolean includeToken = includeRedundantTokens;
		if(children.isEmpty()) {
			return currentIndentation+"<"+id()+" token=\""+xmlEscape(sourceToken.data)+"\"/>";
		} else if(children.size()==1) {
			includeToken |= children.get(0).sourceToken!=this.sourceToken;
			childrenBuilder.append(children.get(0).toIndentedXML(nextIndentationSpaces,stride,includeRedundantTokens)).append('\n');
		} else {
			includeToken |= children.get(0).sourceToken!=this.sourceToken;
			childrenBuilder.append(children.get(0).toIndentedXML(nextIndentationSpaces,stride,includeRedundantTokens)).append('\n');
			for(int i=1;i<children.size();i++) {
				childrenBuilder.append(children.get(i).toIndentedXML(nextIndentationSpaces,stride,includeRedundantTokens)).append('\n');
			}
		}
		String result = currentIndentation+"<"+id()+(includeToken ? " token=\""+xmlEscape(sourceToken.data)+"\"":"")+">\n"
				+childrenBuilder.toString()+
				currentIndentation+"</"+id()+">";
		return result;
	}
	public String toXML(int indentation, boolean includeRedundantTokens) {
		return this.toIndentedXML(0, indentation, includeRedundantTokens);
	}
	private String jsEscape(String data) {
		return data.replaceAll("[\"\\\\]", "\\\\$0");
	}
	private String xmlEscape(String data) {
		/*
		 *	"   &quot;
			'   &apos;
			<   &lt;
			>   &gt;
			&   &amp;
		 */
		return data.replace("&", "&amp;")
				.replace(">", "&gt;")
				.replace("<", "&lt;")
				.replace("'", "&apos;")
				.replace("\"", "&quot;");
	}
	public SyntaxTree collapse() {
		
		
		SyntaxTree result;
		if(this.children.size()==1 && this.children.get(0).sourceToken==this.sourceToken && this.originalState!=null && this.originalState.helperState) {
			return this.children.get(0).collapse();
		}
		
		
		if(this.originalElement!=null)
			result = new SyntaxTree(originalElement, sourceToken);
		else
			result = new SyntaxTree(originalState, sourceToken);
		
		children.forEach(child -> result.addChild(child.collapse()));
		return result;
	}
}
