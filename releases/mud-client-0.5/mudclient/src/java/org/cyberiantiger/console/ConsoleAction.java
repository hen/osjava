package org.cyberiantiger.console;

public interface ConsoleAction {

    public void apply(Console console);

    public boolean isEndOfLine();

    public void appendToBuffer(StringBuffer buffer);

}
