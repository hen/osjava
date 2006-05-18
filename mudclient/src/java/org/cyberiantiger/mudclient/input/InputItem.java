package org.cyberiantiger.mudclient.input;

public class InputItem {

    private String msg;
    private int depth;

    public InputItem(String msg, int depth) {
	this.msg = msg;
	this.depth = depth;
    }

    public String getMessage() {
	return msg;
    }

    public int getDepth() {
	return depth;
    }

}
