package org.cyberiantiger.telnet;

import java.io.ByteArrayOutputStream;

public class TelnetSubOption extends AbstractTelnetSubOption {

    private byte[] data;

    public TelnetSubOption(int suboption, byte[] data) {
	super(SB, suboption);
	this.data = data;
    }

    public byte[] getData() {
	return data;
    }

    public String toString() {
	return super.toString() + " " + dataToString(data) + " IAC SE";
    }

    public byte[] getBytes() {
	ByteArrayOutputStream out = new ByteArrayOutputStream(40);
	out.write(IAC);
	out.write(SB);
	out.write(suboption);
	for(int i=0;i<data.length;i++) {
	    int b = data[i];
	    if(b == IAC) {
		out.write(IAC);
		out.write(IAC);
	    } else {
		out.write(b);
	    }
	}
	out.write(IAC);
	out.write(SE);
	return out.toByteArray();
    }
}
