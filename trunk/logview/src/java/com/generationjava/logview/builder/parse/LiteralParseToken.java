package com.generationjava.logview.builder.parse;

public class LiteralParseToken extends AbstractParseToken {

    public LiteralParseToken(String value) {
        super(value, ParseToken.LITERAL);
    }

}
