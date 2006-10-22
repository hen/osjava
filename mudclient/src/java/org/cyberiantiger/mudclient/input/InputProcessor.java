package org.cyberiantiger.mudclient.input;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

import org.cyberiantiger.mudclient.ui.Connection;
import org.cyberiantiger.mudclient.net.Display;
import org.cyberiantiger.mudclient.config.ClientConfiguration;

public class InputProcessor implements Connection {
    
    public static final int MAX_RECURSE = 16;

    private Display display;
    private Connection conn;
    private Map aliases = new HashMap();
    private StringBuffer output = new StringBuffer();

    private boolean parseInput = true;

    public InputProcessor(
	    Connection conn,
	    Display display) 
    {
	this.display = display;
	this.conn = conn;
    }

    public void command(String sourceId, String msg) {
	InputStack stack = new InputStack(MAX_RECURSE);
	StringBuffer tmp = new StringBuffer();
	boolean inEscape = false;
	for (int i = 0; i<msg.length(); i++) {
	    char ch = msg.charAt(i);
	    if (!inEscape) {
		if (ch == ';') {
		    stack.addItem(new InputItem(tmp.toString(), 0));
		    tmp.setLength(0);
		} else if (ch == '\\') {
		    inEscape = true;
		} else {
		    tmp.append(ch);
		}
	    } else {
		tmp.append(ch);
		inEscape = false;
	    }
	}
	stack.addItem(new InputItem(tmp.toString(), 0));
	while (stack.hasMoreItems()) {
	    processInput(sourceId, stack);
	}
	conn.command(sourceId, output.toString());
	output.setLength(0);
    }

    public void connect() {
	conn.connect();
    }

    public void disconnect() {
	conn.disconnect();
    }

    public void setWindowSize(int w, int h) {
	conn.setWindowSize(w,h);
    }

    public ClientConfiguration getConfiguration() {
	return conn.getConfiguration();
    }

    protected void processInput(String sourceId, InputStack stack) {
	InputItem next = stack.nextItem();
	String msg = next.getMessage();
	List aliases = (List) getConfiguration().getAliases( sourceId );
	if (aliases != null) {
	    Iterator i = aliases.iterator();
	    while (i.hasNext()) {
		Alias alias = (Alias) i.next();
		if (alias.apply(this, stack, next)) {
		    return;
		}
	    }
	}
	// Didn't match any aliases.
	addCommand(msg);
    }

    public void addCommand(String command) {
	output.append(command);
	output.append('\n');
	display.localEcho(command);
    }

    public void exit() {
        conn.exit();
    }
}
