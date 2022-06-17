package lexing;
import java.io.File;

public class Token {
	public final TokenType type;
	public final String data;
	public final File sourceFile;
	public final int line;
	public final int location;

	public Token(TokenType type, String data, File sourceFile, int line, int location) {
		super();
		this.type = type;
		this.data = data;
		this.sourceFile = sourceFile;
		this.line = line;
		this.location = location;
	}

	@Override
	public String toString() {
		return "Token [type=" + type + ", data=" + data + ", sourceFile=" + sourceFile + ", line=" + line
				+ ", location=" + location + "]\n";
	}
}
