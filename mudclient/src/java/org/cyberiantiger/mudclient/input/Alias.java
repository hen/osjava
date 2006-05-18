package org.cyberiantiger.mudclient.input;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Alias {
    private String regexp;
    private String replace;
    private boolean terminate;

    private Pattern pattern;
    private List replacement;

    public Alias(String regexp, String replace, boolean terminate) {
	setRegexp(regexp);
	setReplace(replace);
	setTerminate(terminate);
    }

    /**
     * Apply this alias to an InputItem.
     *
     * @return true on a match, false otherwise.
     */
    public boolean apply(
	    InputProcessor input, InputStack stack, InputItem item
	    ) 
    {
	String msg = item.getMessage();
	Matcher matcher = pattern.matcher(msg);
	if (!matcher.matches()) {
	    return false;
	}

	if (terminate) {
	    Iterator i = replacement.iterator();
	    while (i.hasNext()) {
		Line line = (Line) i.next();
		input.addCommand(line.replace(matcher));
	    }
	} else {
	    int i = replacement.size();
	    while (i-- > 0) {
		Line line = (Line) replacement.get(i);
		stack.pushItem(
			new InputItem(
			    line.replace(matcher),
			    item.getDepth() + 1
			    )
			);
	    }
	}
	return true;
    }

    /**
     * Get the regular expression string used to create this alias.
     */
    public String getRegexp() {
	return regexp;
    }

    /**
     * Set the regular expression string.
     *
     * @throws PatternSyntaxException if the regexp is invalid.
     */
    public void setRegexp(String regexp) {
	pattern = Pattern.compile(regexp);
	this.regexp = regexp;
    }

    /**
     * Get the replacement string for this alias.
     */
    public String getReplace() {
	return replace;
    }

    /**
     * Set the replacement string for this alias.
     *
     * @throws ReplacementSyntaxException
     */
    public void setReplace(String replace) {
	replacement = compileReplacement(replace);
	this.replace = replace;
    }

    /**
     * Set whether this alias's replacement bypasses other aliases.
     */
    public void setTerminate(boolean terminate) {
	this.terminate = terminate;
    }

    /**
     * Get whether this alias's replacement bypasses other aliases.
     */
    public boolean getTerminate() {
	return terminate;
    }

    /**
     * Compile a replacement string.
     *
     * "\" - escape character.
     * ";" - new line.
     * "${0}" - matches entire matched text.
     * "${1}" - matches group 1 from the regexp.
     * 
     * @throws ReplacementSyntaxException
     */
    protected List compileReplacement(String replace) {
	List ret = new ArrayList();
	Line currentLine = new Line();
	StringBuffer currentToken = new StringBuffer();
	int state = 0;

	for (int i = 0; i<replace.length(); i++) {
	    char ch = replace.charAt(i);


	    switch (state) {
		case 0: /* Default */
		    if (ch == '\\') {
			state = 1;
		    } else if (ch == ';') {
			if (currentToken.length() > 0) {
			    currentLine.addToken(
				    new StringToken(currentToken.toString())
				    );
			    currentToken.setLength(0);
			}
			ret.add(currentLine);
			currentLine = new Line();
		    } else if (ch == '$') {
			if(currentToken.length() > 0) {
			    currentLine.addToken(
				    new StringToken(currentToken.toString())
				    );
			    currentToken.setLength(0);
			}
			state = 2;
		    } else {
			currentToken.append(ch);
		    }
		    break;
		case 1: /* Escape */
		    currentToken.append(ch);
		    state = 0;
		    break;
		case 2: /* After $ */
		    if (ch != '{') {
			throw new ReplacementSyntaxException("$ not followed by {");
		    }
		    state = 3;
		    break;
		case 3: /* After { before } */
		    if (ch == '}') {
			if (currentToken.length() == 0) {
			    throw new ReplacementSyntaxException("$ reference didn't contain anything");
			}
			currentLine.addToken(
				new ReferenceToken(Integer.parseInt(currentToken.toString()))
				);
			currentToken.setLength(0);
			state = 0;
		    } else if (ch >= '0' && ch <= '9') {
			currentToken.append(ch);
		    } else {
			throw new ReplacementSyntaxException("$ reference not a number");
		    }
		    break;
	    }
	}
	if (state != 0) {
	    throw new ReplacementSyntaxException("Alias ended mid token");
	}
	if (currentToken.length() > 0) {
	    currentLine.addToken(new StringToken(currentToken.toString()));
	}
	ret.add(currentLine);
	return ret;
    }

    /**
     * Class to replacement a replacement line.
     */
    private static class Line {
	/**
	 * List of replacement tokens which make up this line.
	 */
	private List tokens = new ArrayList();

	/**
	 * Return the line replacement for the specific match.
	 */
	public String replace(Matcher match) {
	    StringBuffer ret = new StringBuffer();
	    Iterator i = tokens.iterator();
	    while (i.hasNext()) {
		Token t = (Token) i.next();
		ret.append(t.replace(match));
	    }
	    return ret.toString();
	}

	/**
	 * Add a token to this replacement line.
	 */
	public void addToken(Token token) {
	    tokens.add(token);
	}

	public String toString() {
	    StringBuffer ret = new StringBuffer();
	    Iterator i = tokens.iterator();
	    while(i.hasNext()) {
		ret.append(i.next().toString());
	    }
	    return ret.toString();
	}
    }

    /**
     * An interface to represent a replacment token.
     */
    private static interface Token {
	/**
	 * Return the replacement for this token against match.
	 */
	public String replace(Matcher match);
    }

    /**
     * A replacment token which represents a string which doesn't change.
     */
    private static class StringToken implements Token {
	private String msg;

	public StringToken(String msg) {
	    this.msg = msg;
	}

	public String replace(Matcher match) {
	    return msg;
	}

	public String toString() {
	    return msg;
	}
    }

    /**
     * A replacement token which represents a reference to a matched group.
     */
    private static class ReferenceToken implements Token {
	private int offset;

	public ReferenceToken(int offset) {
	    this.offset = offset;
	}

	public String replace(Matcher match) {
	    return match.group(offset);
	}

	public String toString() {
	    return "${"+offset+"}";
	}
    }

    /* XXX: Should add VariableReference and FunctionReference */
}
