package com.generationjava.logview.builder;

import java.util.ArrayList;

import com.generationjava.lang.StringW;

import com.generationjava.logview.Log;
import com.generationjava.logview.LogBuilder;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogSource;
import com.generationjava.logview.LogTypes;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.SimpleLogEvent;
import com.generationjava.logview.log.SimpleLogField;

import com.generationjava.logview.builder.parse.*;

// more advanced version, takes:

//127.0.0.1 - - [16/Sep/2001:16:08:42 -0500] "GET /tomcat.gif HTTP/1.1" 200 1934


//${ip:IP} ${username:STRING} ${email:EMAIL} [${date:DATE}] "${cmd:STRING} ${filename:FILE} $protocol" ${code:INTEGER} ${size:INTEGER}

/// TODO:
//%I %u %E [%D] ...
//<parse:line><parse:token name="ip" ="IP"/> <parse:token name="username" ="STRING"/> <parse:token name="email" ="EMAIL"/> [<parse:token name="date" ="DATE"/>] "<parse:token name="cmd" ="STRING"/> <parse:token name="filename" ="FILE"/> <parse:token name="size" ="STRING"/>" <parse:token name="code" ="INTEGER"/> <parse:token name="size" ="INTEGER"/></parse:line>

//
//    then it first preparses to build a parse tree. have a:
//      ParseToken, VariableParseToken and LiteralParseToken
//    parsing is a case of grabbing all up until the next 
//    occurence of the parse token, and sticking it in the variable.
//    a future version on top of this would use regexp, ie) \w
public class FormattedLogBuilder extends AbstractLogBuilder {

    // parsing state
    static private final int VOID       = 0;
    static private final int OPEN_VAR   = 1;
    static private final int IN_NAME    = 2;
    static private final int IN_TYPE    = 3;

    private ParseToken[] tokens;
    private String[] headers;

    private FormattedLogBuilder() {
    }

    public FormattedLogBuilder(String format, LogSource source) {
        setFormat(format);
        setSource(source);
    }

    public String[] getHeaders() {
        return this.headers;
    }

    public void setFormat(String format) {
        this.tokens = parseFormat(format);
//        com.generationjava.util.Debug.out.println(this.tokens);
        ArrayList list = new ArrayList();
        for(int i=0; i<this.tokens.length; i++) {
            if(tokens[i] instanceof VariableParseToken) {
                list.add(tokens[i].getValue());
//                System.err.println("Adding header: "+tokens[i].getValue());
            }
        }
        this.headers = (String[])list.toArray( new String[0] );
    }

    // Currently handles this ${var:TYPE} system
    //${ip:IP} ${username:STRING} ${email:EMAIL} [${date:DATE}] "${cmd:STRING} ${filename:FILE} $protocol" ${code:INTEGER} ${size:INTEGER}
    //
    // A very simple state parser. 
    public ParseToken[] parseFormat(String format) {
        ArrayList list = new ArrayList();

        ParseToken pt = null;
        char[] chrs = format.toCharArray();
        int sz = chrs.length;

        int state = VOID;

        StringBuffer name    = new StringBuffer();
        StringBuffer type    = new StringBuffer();
        StringBuffer literal = new StringBuffer();

        for( int i=0; i<sz; i++) {
            char ch = chrs[i];

            // simple state-like machine parser
            switch(state) {

                case VOID: {
                    if(ch == '$') {
                        state = OPEN_VAR;

                        // create literal?
                        if(literal.length() != 0) {
                            pt = new LiteralParseToken(literal.toString());
                            literal.setLength(0);
                            list.add(pt);
                        }
                    } else {
                        literal.append(ch);
                    }
                }
                break;

                case OPEN_VAR: {
                    if(ch == '{') {
                        state = IN_NAME;
                    } else {
                        literal.append("$");
                        literal.append(ch);
                        state = VOID;
                    }
                }
                break;

                case IN_NAME: {
                    if(ch == ':') {
                        state = IN_TYPE;
                    } else
                    if(ch == '}') {
                        state = VOID;
                        // do new token!
                        pt = new VariableParseToken(name.toString(), "STRING");
                        name.setLength(0);
                        list.add(pt);
                    } else {
                        name.append(ch);
                    }
                }
                break;

                case IN_TYPE: {
                    if(ch == '}') {
                        state = VOID;
                        
                        // do new token!
                        pt = new VariableParseToken(name.toString(), type.toString());
                        name.setLength(0);
                        type.setLength(0);
                        list.add(pt);
                    } else {
                        type.append(ch);
                    }
                }
                break;

            }
        }

        return (ParseToken[])list.toArray(new ParseToken[0]);
    }

    public LogEvent parseLogEvent() throws LogViewException {
        SimpleLogEvent event = new SimpleLogEvent();

        String entry = getSource().nextEntry();
        int idx = 0;

        final int sz = this.tokens.length;
        // too simple. needs optimising.
        // literal parse tokens don't need to check 
        // that they match after a variable token has
        for(int i=0; i<sz; i++) {
            ParseToken token = this.tokens[i];
            if(token instanceof LiteralParseToken) {
                
                // skip past literal
                String literal = token.getValue();
//                    System.err.println("Skipping: ["+literal+"]");
                if(entry.indexOf(literal, idx) == idx) {
                    idx += literal.length();
                } else {
                    /// TODO: Add more data
                    throw new LogViewException("Unable to find literal '"+literal+"'");
                }

            } else {

                // variable token!
                String name = token.getValue();

                int nextLiteral = -1;
                String substr;
                
                // check not on last token
                if(i != sz-1) {
                    ParseToken t2 = tokens[i+1];

                    if( t2 instanceof LiteralParseToken ) {
                        String literal = t2.getValue();
                        nextLiteral = entry.indexOf(literal, idx);
                        if(nextLiteral == -1) {
                            /// TODO: Add more data
                            throw new LogViewException("Unable to find literal '"+literal+"' in look-ahead.");
                        }

                    } else {
                        throw new LogViewException("Illegal to have two adjoining Variables");
                    }

                    substr = entry.substring(idx, nextLiteral);
                } else {
                    substr = entry.substring(idx);
                }

                SimpleLogField field = new SimpleLogField(name, LogTypes.getLogType(token.getType()), substr );
                event.set(name, field);
                idx = nextLiteral;
//                    System.err.println("Adding field: "+name);
            }
        }

        return event;
    }

    public Object cloneObject() throws CloneNotSupportedException {
        FormattedLogBuilder builder = new FormattedLogBuilder();
        builder.tokens = this.tokens;
        builder.headers = this.headers;
        builder.setSource( this.getSource() );
        return builder;
    }

}
