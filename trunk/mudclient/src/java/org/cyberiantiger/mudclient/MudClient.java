package org.cyberiantiger.mudclient;

import java.util.*;
import org.cyberiantiger.console.ConsoleAction;
import org.cyberiantiger.mudclient.parser.Parser;

public interface MudClient {

    /**
     * Receive a ConsoleAction which should be sent to the appropriate console.
     * NOTE: Called from Parser.
     */
    public void consoleAction(ConsoleAction action);

    /**
     * Exit the client
     */
    public void exit();

    /**
     * Set the parser
     */
    public void setParser(Parser parser);

    /**
     * Connect
     */
    public void connect();

    /**
     * Disconnect
     */
    public void disconnect();

    /**
     * Send a command to the mud
     */
    public void command(String sourceId, String command);

    /**
     * Send a window size to the mud
     */
    public void setWindowSize(int w, int h);

    /**
     * Get the custom output name for a primary message class
     */
    public String getCustomOutputName(String pClass); 

    /**
     * Get the List of messages classes for a particular output name.
     */
    public List getCustomOutputTypes(String outputName);

    /**
     * Called by the MudConnection, when it's status changes.
     */
    public void connectionStatusChanged(int newStatus);

    /**
     * Called by the MudConnection to toggle local echo
     */
    public void connectionDoLocalEcho(boolean echo);
}
