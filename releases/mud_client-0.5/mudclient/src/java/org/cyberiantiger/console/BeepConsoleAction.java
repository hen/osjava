package org.cyberiantiger.console;

public class BeepConsoleAction extends AbstractConsoleAction {

    public BeepConsoleAction() {
    }

    public void apply(Console con) {
	con.beep();
    }

}
