package org.cyberiantiger.console;

public class ReverseConsoleAction extends AbstractConsoleAction {

    private boolean reverse;

    public ReverseConsoleAction(boolean reverse) {
	this.reverse = reverse;
    }

    public void apply(Console con) {
	con.setReverse(reverse);
    }
}
