package org.cyberiantiger.mudclient.parser;

import org.cyberiantiger.mudclient.MudClient;


public interface Parser {

    /**
     * Push a character to this parser.
     *
     * @return The parser to push the next character to, may not always be
     * this instance of parser
     */
    public void putChar(char ch);

    /**
     * Tell this parser to flush it's output to client.
     */
    public void flush(MudClient client);

    /**
     * Get the name of this parser
     */
    public String getName();

    /**
     * Should we change the parser
     */
    public boolean changeParser();

    /**
     * Get the new parser in the event that it should be changed.
     */
    public Parser getNewParser();
}
