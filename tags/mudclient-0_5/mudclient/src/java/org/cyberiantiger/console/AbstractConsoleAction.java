package org.cyberiantiger.console;

public abstract class AbstractConsoleAction implements ConsoleAction {

    public boolean isEndOfLine() {
	return false;
    }

    public void appendToBuffer(StringBuffer buffer) {
    }

}
