package org.cyberiantiger.console;

public class SetCursorXConsoleAction extends AbstractConsoleAction {

    private int x;

    public SetCursorXConsoleAction(int x) {
	this.x = x;
    }

    public boolean isEndOfLine() {
	return true;
    }

    public void apply(Console con) {
	con.setCursorX(x);
    }
}
