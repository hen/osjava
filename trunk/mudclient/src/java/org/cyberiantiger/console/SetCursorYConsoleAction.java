package org.cyberiantiger.console;

public class SetCursorYConsoleAction extends AbstractConsoleAction {

    private int y;

    public SetCursorYConsoleAction(int y) {
	this.y = y;
    }

    public boolean isEndOfLine() {
	return true;
    }

    public void apply(Console con) {
	con.setCursorY(y);
    }
}
