package org.cyberiantiger.console;

public class FlashConsoleAction extends AbstractConsoleAction {

    private boolean flash;

    public FlashConsoleAction(boolean flash) {
	this.flash = flash;
    }

    public void apply(Console con) {
	con.setFlash(flash);
    }
}
