package org.cyberiantiger.console;

public class BoldConsoleAction extends AbstractConsoleAction {

    private boolean bold;

    public BoldConsoleAction(boolean bold) {
	this.bold = bold;
    }

    public void apply(Console con) {
	con.setBold(bold);
    }

}
