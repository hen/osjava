package org.cyberiantiger.telnet;

public abstract class AbstractTelnetSubOption extends AbstractTelnetOption {

    protected int suboption;

    public AbstractTelnetSubOption(int option, int suboption) {
	super(option);
	this.suboption = suboption;
    }

    public int getSubOption() {
	return suboption;
    }

    public byte[] getBytes() {
	return new byte[] { (byte) IAC, (byte) option, (byte) suboption };
    }

    public String toString() {
	return super.toString() + " " + optionToString(suboption);
    }
}
