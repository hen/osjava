package org.cyberiantiger.mudclient.parser;

import java.util.*;
import org.cyberiantiger.mudclient.*;
import org.cyberiantiger.console.*;

public class ANSIParser implements Parser {

    public static final int NONE = 0;
    public static final int ELECLIENT = 1;

    public static final int TEXT = 0;
    public static final int ANSI1 = 1;
    public static final int ANSI2 = 2;
    public static final int ANSI3 = 3;
    public static final int ANSI4 = 4;

    private char[] buffer;
    private int offset;
    private int last_offset;
    private int state;
    private int parserDetect = NONE;
    StringBuffer ansi = new StringBuffer();

    private List actions = new ArrayList();

    public ANSIParser() {
	buffer = new char[2048];
	offset = 0;
	last_offset = 0;
    }

    public void putChar(char ch) {
	if(offset == buffer.length) {
	    char[] newBuff = new char[2048];
	    System.arraycopy(buffer,last_offset,newBuff,0,buffer.length - last_offset);
	    offset = buffer.length - last_offset;
	    last_offset = 0;
	    buffer = newBuff;
	}
	switch(state) {
	    case TEXT:
		switch(ch) {
		    case '\n':
			actions.add(new StringConsoleAction(buffer,last_offset,offset-last_offset));
			last_offset = offset;
			actions.add(new SetCursorXConsoleAction(0));
			break;
		    case '\r':
			actions.add(new StringConsoleAction(buffer,last_offset,offset-last_offset));
			last_offset = offset;
			actions.add(new MoveCursorYConsoleAction(1));
			break;
		    case '\007':
			actions.add(new StringConsoleAction(buffer,last_offset,offset-last_offset));
			last_offset = offset;
			actions.add(new BeepConsoleAction());
			break;
		    case '\014':
			actions.add(new ClearScreenConsoleAction());
			last_offset = offset;
			break;
		    case 27:
			actions.add(new StringConsoleAction(buffer,last_offset,offset-last_offset));
			last_offset = offset;
			state = ANSI1;
			break;
		    default:
			buffer[offset++] = ch;
		}
		break;
	    case ANSI1:
		switch(ch) {
		    case '[':
			state = ANSI2;
			break;
		    case 'D':
			// Elephant External Client Detection !
			parserDetect = ELECLIENT;
		    default:
			state = TEXT;
			break;
		}
		break;
	    case ANSI2:
		if(
			(ch >= 'A' && ch <= 'Z')  ||
			(ch >= 'a' && ch <= 'z')
		  )
		{
		    switch(ch) {
			case 'm':
			    StringTokenizer tokenizer = 
			    new StringTokenizer(ansi.toString(),";");
			    while(tokenizer.hasMoreTokens()) {
				try {
				    int i = Integer.parseInt(tokenizer.nextToken());
				    ConsoleAction action = null;
				    switch (i) {
					case 0:
					    actions.add(new BoldConsoleAction(false));
					    actions.add(new FlashConsoleAction(false));
					    actions.add(new ReverseConsoleAction(false));
					    break;
					case 1:
					    action=new BoldConsoleAction(true);
					    break;
					case 4:
					    // Underscore -> Not implemented.
					    break;
					case 5:
					    action=new FlashConsoleAction(true);
					    break;
					case 7:
					    action=new ReverseConsoleAction(true);
					    break;
					case 8:
					    // Conceal -> Not implemented.
					    break;
					case 30:
					    action=new ForegroundConsoleAction(Console.BLACK);
					    break;
					case 31:
					    action=new ForegroundConsoleAction(Console.RED);
					    break;
					case 32:
					    action=new ForegroundConsoleAction(Console.GREEN);
					    break;
					case 33:
					    action=new ForegroundConsoleAction(Console.YELLOW);
					    break;
					case 34:
					    action=new ForegroundConsoleAction(Console.BLUE);
					    break;
					case 35:
					    action=new ForegroundConsoleAction(Console.MAGENTA);
					    break;
					case 36:
					    action=new ForegroundConsoleAction(Console.CYAN);
					    break;
					case 37:
					    action=new ForegroundConsoleAction(Console.WHITE);
					    break;
					case 40:
					    action=new BackgroundConsoleAction(Console.BLACK);
					    break;
					case 41:
					    action=new BackgroundConsoleAction(Console.RED);
					    break;
					case 42:
					    action=new BackgroundConsoleAction(Console.GREEN);
					    break;
					case 43:
					    action=new BackgroundConsoleAction(Console.YELLOW);
					    break;
					case 44:
					    action=new BackgroundConsoleAction(Console.BLUE);
					    break;
					case 45:
					    action=new BackgroundConsoleAction(Console.MAGENTA);
					    break;
					case 46:
					    action=new BackgroundConsoleAction(Console.CYAN);
					    break;
					case 47:
					    action=new BackgroundConsoleAction(Console.WHITE);
					    break;

				    }
				    if(action != null) {
					actions.add(action);
				    }
				} catch (NumberFormatException nfe) {
				}
			    }
			    break;
			case 'H':
			    if(ansi.length() == 0){
				actions.add(new ClearScreenConsoleAction());
			    }
			    break;
			default:
			    ansi.append(ch);
			    System.out.println("Unknown escape string: \""+ansi+"\"");
		    }
		    ansi.setLength(0);
		    state = TEXT;
		} else {
		    ansi.append(ch);
		}
		break;
	}
    }

    public void flush(ConsoleWriter client) {
	Iterator i = actions.iterator();
	while(i.hasNext()) {
	    ConsoleAction action = (ConsoleAction) i.next();
	    client.consoleAction(action);
	}
	actions.clear();
	client.consoleAction(
		new StringConsoleAction(buffer,last_offset,offset-last_offset)
		);
	last_offset = offset;
    }

    public String getName() {
	return "ANSI";
    }

    public boolean changeParser() {
	return parserDetect != NONE;
    }

    public Parser getNewParser() {
	switch(parserDetect) {
	    case ELECLIENT:
		return new ElephantMUDParser();
	    default:
		return this;
	}
    }
}
