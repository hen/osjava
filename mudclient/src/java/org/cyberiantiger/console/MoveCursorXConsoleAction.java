package org.cyberiantiger.console;

public class MoveCursorXConsoleAction extends AbstractConsoleAction {

    private int x;

    public MoveCursorXConsoleAction(int x) {
	this.x = x;
    }

    public boolean isEndOfLine() {
	return true;
    }

    public void apply(Console con) {
	con.moveCursorX(x);
    }
}
