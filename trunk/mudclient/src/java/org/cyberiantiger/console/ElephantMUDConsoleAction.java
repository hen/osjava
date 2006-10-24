package org.cyberiantiger.console;

import java.util.Map;
import java.util.HashMap;
import java.util.regex.*;
import java.io.*;

public class ElephantMUDConsoleAction extends AbstractConsoleAction {

    private static Pattern mudMode = Pattern.compile("\\A\\(/(0|\".*\"),(0|\".*\"),(0|\".*\"),(-?\\d+),(-?\\d+),(-?\\d+),/\\)(.*)\\z$",Pattern.DOTALL);

    private static Map pinkFish = new HashMap();

    public final static int NO_WRAP = 1;
    public final static int NO_BLOCK = 2;
    public final static int NO_BUFFER = 4;
    public final static int IS_INVIS = 8;
    public final static int IS_DEAD = 16;
    public final static int IS_CHANNEL = 32;
    public final static int NO_NEWLINE = 64;

    static {
	ConsoleAction tmp;
	pinkFish.put("RESET",new ResetConsoleAction());
	pinkFish.put("BOLD",new BoldConsoleAction(true));
	pinkFish.put("FLASH",new FlashConsoleAction(true));
	pinkFish.put("RED",new ForegroundConsoleAction(Console.RED));
	pinkFish.put("YELLOW",tmp=new ForegroundConsoleAction(Console.YELLOW));
	pinkFish.put("ORANGE",tmp);
	pinkFish.put("GREEN",new ForegroundConsoleAction(Console.GREEN));
	pinkFish.put("CYAN",new ForegroundConsoleAction(Console.CYAN));
	pinkFish.put("BLUE",new ForegroundConsoleAction(Console.BLUE));
	pinkFish.put("MAGENTA",new ForegroundConsoleAction(Console.MAGENTA));
	pinkFish.put("BLACK",new ForegroundConsoleAction(Console.BLACK));
	pinkFish.put("WHITE",new ForegroundConsoleAction(Console.WHITE));
	pinkFish.put("B_RED",new BackgroundConsoleAction(Console.RED));
	pinkFish.put("B_YELLOW",tmp=new BackgroundConsoleAction(Console.YELLOW));
	pinkFish.put("B_ORANGE",tmp);
	pinkFish.put("B_GREEN",new BackgroundConsoleAction(Console.GREEN));
	pinkFish.put("B_CYAN",new BackgroundConsoleAction(Console.CYAN));
	pinkFish.put("B_BLUE",new BackgroundConsoleAction(Console.BLUE));
	pinkFish.put("B_MAGENTA",new BackgroundConsoleAction(Console.MAGENTA));
	pinkFish.put("B_BLACK",new BackgroundConsoleAction(Console.BLACK));
	pinkFish.put("B_WHITE",new BackgroundConsoleAction(Console.WHITE));
	tmp = new NullConsoleAction();
	pinkFish.put("ENDTERM",tmp);
	pinkFish.put("TERMSIZE",tmp);
	pinkFish.put("TOINPUT",tmp);
	pinkFish.put("TOMAIN",tmp);
	pinkFish.put("TOSTATUS",tmp);
	pinkFish.put("SC",tmp);
	pinkFish.put("RC",tmp);
	pinkFish.put("CLS",new ClearScreenConsoleAction());
	pinkFish.put("CLEOL",tmp);
	pinkFish.put("BELL",new BeepConsoleAction());

	// The following action(s) are not used by Elephant, but included for
	// completeness
	pinkFish.put("REVERSE",new ReverseConsoleAction(true));
    }

    private String pClass;
    private String sClass;
    private String orig;
    private int flags;
    private int indent;
    private int wrapAt;

    private String data;

    public ElephantMUDConsoleAction(String data) {
	Matcher m = mudMode.matcher(data);
	if(m.matches()) {
	    String tmp;
	    tmp = m.group(1);
	    if(tmp.length()>1) pClass = tmp.substring(1,tmp.length()-1);
	    tmp = m.group(2);
	    if(tmp.length()>1) sClass = tmp.substring(1,tmp.length()-1);
	    tmp = m.group(3);
	    if(tmp.length()>1) orig = tmp.substring(1,tmp.length()-1);
	    flags = Integer.parseInt(m.group(4));
	    indent = Integer.parseInt(m.group(5));
	    wrapAt = Integer.parseInt(m.group(6));
	    this.data = m.group(7);
	} else {
	    this.data = data;
	}
    }

    public ElephantMUDConsoleAction(
	    String pClass,String sClass,String orig,
	    int flags, int indent, int wrapAt, String data
	    ) 
    {
	this.pClass = pClass;
	this.sClass = sClass;
	this.orig = orig;
	this.flags = flags;
	this.indent = indent;
	this.wrapAt = wrapAt;
	this.data = data;
    }

    public String getPrimaryClass() {
	return pClass;
    }

    public String getSecondaryClass() {
	return sClass;
    }

    public String getOrigin() {
	return orig;
    }

    public int getFlags() {
	return flags;
    }

    public int getIndent() {
	return indent;
    }

    public int getWrapAt() {
	return wrapAt;
    }

    public String getData() {
	return data;
    }

    public void apply(Console con) {
	int w = con.getWidth();
	int h = con.getHeight();
	String[] lines = data.split("\r?\n");
	
	// Check cursor position: If not at 0, newline
	if(con.getCursorX() != 0) {
	    con.setCursorX(0);
	    con.moveCursorY(1);
	}

	for(int i=0;i<lines.length;i++) {
	    String[] tokens = lines[i].split("%\\^");
	    for(int j=0;j<tokens.length;j++) {
		String token = tokens[j];
		ConsoleAction action = (ConsoleAction) pinkFish.get(token);
		if(action != null) {
		    action.apply(con);
		} else {
                    wrapString(token, con);
		}
	    }
	    // After all but last line
	    if(i != lines.length-1) {
		con.setCursorX(0);
		con.moveCursorY(1);
	    }
	}
	// Hack, prompt and status msg have invalid flags !
	if(
		!(
		    "prompt".equals(pClass) || 
		    "status".equals(pClass) || 
		    (flags & NO_WRAP) != 0
		 )
	  )
	{
	    con.setCursorX(0);
	    con.moveCursorY(1);
	}
	con.setForeground(con.WHITE);
	con.setBackground(con.BLACK);
	con.setBold(false);
	con.setFlash(false);
    }

    public void appendToBuffer(StringBuffer buffer) {
    }

    private void wrapString(String str, Console con) {
        int w = con.getWidth();
        int x;
        char[] c = str.toCharArray();
        int i = 0;
LOOP:
        while (i < c.length) {
            x = con.getCursorX();
            if ( c.length - i <= w - x ) {
                con.drawString(c, i, c.length - i);
                break LOOP;
            }
            for (int j = i + w - x; j > i; j--) {
                if (c[j] == ' ') {
                    con.drawString(c, i, j - i);
                    i = j + 1;
                    con.setCursorX(0);
                    con.moveCursorY(1);
                    continue LOOP;
                }
            }
            if ( x != 0 ) {
                con.setCursorX(0);
                con.moveCursorY(1);
                continue LOOP;
            }
            for (int j = i + w - x + 1; j < c.length; j++) {
                if (c[j] == ' ') {
                    con.drawString(c, i, j - i);
                    i = j + 1;
                    con.setCursorX(0);
                    con.moveCursorY(1);
                    continue LOOP;
                }
            }
            con.drawString(c, i, c.length - i);
            break LOOP;
        }
    }
}
