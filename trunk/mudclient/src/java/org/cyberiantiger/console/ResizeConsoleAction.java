package org.cyberiantiger.console;

public class ResizeConsoleAction extends AbstractConsoleAction {

    private int x;
    private int y;

    public ResizeConsoleAction(int x, int y) {
	this.x = x;
	this.y = y;
    }

    public void apply(Console con) {
	con.resize(x,y);
    }
}
