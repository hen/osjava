package org.cyberiantiger.mudclient.input;

/**
 * Exception for when parsing a replacement string fails.
 */
public class ReplacementSyntaxException extends RuntimeException {

    public ReplacementSyntaxException(String msg) {
	super(msg);
    }

}
