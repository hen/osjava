package org.cyberiantiger.console;

public class ForegroundConsoleAction extends AbstractConsoleAction {

    private int color;

    public ForegroundConsoleAction(int color) {
	this.color = color;
    }

    public void apply(Console con) {
	con.setForeground(color);
    }
}
