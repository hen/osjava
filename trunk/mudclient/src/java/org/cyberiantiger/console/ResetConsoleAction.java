package org.cyberiantiger.console;

public class ResetConsoleAction extends AbstractConsoleAction {

    public ResetConsoleAction() {
    }

    public void apply(Console con) {
	con.setForeground(con.WHITE);
	con.setBackground(con.BLACK);
	con.setBold(false);
	con.setFlash(false);
	con.setReverse(false);
    }
}
