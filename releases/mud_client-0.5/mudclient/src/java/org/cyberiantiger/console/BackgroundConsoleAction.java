package org.cyberiantiger.console;

public class BackgroundConsoleAction extends AbstractConsoleAction {

    private int color;

    public BackgroundConsoleAction(int color) {
	this.color = color;
    }

    public void apply(Console con) {
	con.setBackground(color);
    }
}
