package org.cyberiantiger.mudclient.parser;

import java.util.*;
import org.cyberiantiger.console.*;
import org.cyberiantiger.mudclient.MudClient;

public class ElephantMUDParser implements Parser {

    private List actions = new ArrayList();
    private boolean haveEscape = false;

    StringBuffer temp = new StringBuffer();

    public ElephantMUDParser() {
    }

    public void putChar(char ch) {
	if(haveEscape) {
	    if(ch == 'D') {
		String text = temp.toString();
		temp.setLength(0);
		// XXX: A Hack to get around some messages not being 
		// done correctly
		if(!text.startsWith("(/")) {
		    int i = text.indexOf("(/");
		    actions.add(
			    new ElephantMUDConsoleAction(
				"fail",null,null,0,0,0,text.substring(0,i)
				)
			    );
		    text = text.substring(i);
		}
		actions.add(new ElephantMUDConsoleAction(text));
	    } else {
		// The mud isn't sending us stuff in external client mode !
	    }
	    haveEscape = false;
	} else {
	    if(ch == '\033') {
		haveEscape = true;
	    } else {
		temp.append(ch);
	    }
	}
    }

    public void flush(MudClient client) {
	Iterator i = actions.iterator();
	while(i.hasNext()) {
	    ConsoleAction action = (ConsoleAction) i.next();
	    client.consoleAction(action);
	}
	actions.clear();
    }

    public String getName() {
	return "ELECLIENT";
    }

    public boolean changeParser() {
	return false;
    }

    public Parser getNewParser() {
	return this;
    }
}
