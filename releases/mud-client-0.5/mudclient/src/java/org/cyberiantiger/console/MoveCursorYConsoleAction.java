package org.cyberiantiger.console;

public class MoveCursorYConsoleAction extends AbstractConsoleAction {

    private int y;

    public MoveCursorYConsoleAction(int y) {
	this.y = y;
    }

    public boolean isEndOfLine() {
	return true;
    }

    public void apply(Console con) {
	con.moveCursorY(y);
    }
}
