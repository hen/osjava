package org.cyberiantiger.console;

public class StringConsoleAction extends AbstractConsoleAction {

    private char[] data;
    private int offset;
    private int len;

    public StringConsoleAction(char[] data, int offset, int len) {
	this.data = data;
	this.offset = offset;
	this.len = len;
    }

    public void apply(Console con) {
	con.drawString(data,offset,len);
    }

    public void appendToBuffer(StringBuffer buffer) {
	buffer.append(data,offset,len);
    }
}
