package org.cyberiantiger.console;

public class ClearScreenConsoleAction extends AbstractConsoleAction {

    public void apply(Console con) {
	con.clearScreen();
    }

    public boolean isEndOfLine() {
	return true;
    }
}
