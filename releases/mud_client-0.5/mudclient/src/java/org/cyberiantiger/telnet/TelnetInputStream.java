package org.cyberiantiger.telnet;

import java.io.*;

public abstract class TelnetInputStream extends FilterInputStream {

    byte[] buffer = new byte[2048];
    int off = 0;
    int len = 0;

    boolean inTelnetOpt = false;
    int toptVal = -1;

    public TelnetInputStream(InputStream in) {
	super(new TelnetBufferedInputStream(in));
    }

    /**
     * Fill the buffer in this object.
     */
    private void fillBuffer() throws IOException {
	if(len != 0) return;
	if(in.available() == -1) {
	    // NO MORE DATA !
	    len = -1;
	    return;
	}
	off = 0;
	if(inTelnetOpt) {
	    try {
		((TelnetBufferedInputStream)in).setAllowBlocking(true);
		if(toptVal == -1) {
		    int ch = in.read();
		    if(ch == -1) {
			len = -1;
			return;
		    } else if(ch == TelnetOption.IAC) {
			buffer[len++] = (byte) ch;
		    } else {
			TelnetOption opt = OptionParser.parseOption(in,ch);
			parseTelnetOption(opt);
		    }
		} else {
		    TelnetOption opt = OptionParser.parseOption(in,toptVal);
		    parseTelnetOption(opt);
		}
	    } finally {
		inTelnetOpt = false;
		((TelnetBufferedInputStream)in).setAllowBlocking(false);
	    }
	}
	if(in.available() == 0) {
	    ((TelnetBufferedInputStream)in).fillBuffer();
	}
	int ch;
	while( (ch = in.read()) != -1) {
	    if(ch == TelnetOption.IAC) {
		ch = in.read();
		if(ch != TelnetOption.IAC) {
		    inTelnetOpt = true;
		    toptVal = ch;
		    // Read no more at this point, we have the start of a
		    // telnet option.
		    break;
		} else {
		    buffer[len++] = (byte)ch;
		}
	    } else {
		buffer[len++] = (byte)ch;
	    }
	}
	// Recursive call to ensure we at least have either the end of stream
	// or one byte to read when this method exits.
	if(len == 0) fillBuffer();
    }

    public int read() throws IOException {
	if(len == 0) {
	    fillBuffer();
	}
	if(len == -1) {
	    return -1;
	}
	len--;
	return buffer[off++] & 0xff;
    }

    public int read(byte[] b) throws IOException {
	return read(b,0,b.length);
    }

    public int read(byte[] b, int bOff, int bLen) throws IOException {
	if(len == 0) {
	    fillBuffer();
	}
	if(len == -1) {
	    return -1;
	}
	if(len>bLen) {
	    System.arraycopy(buffer,off,b,bOff,bLen);
	    len -= bLen;
	    off += bLen;
	} else {
	    System.arraycopy(buffer,off,b,bOff,len);
	    bLen = len;
	    len = 0;
	}
	return bLen;
    }

    public int available() throws IOException {
	return len;
    }

    public void close() throws IOException {
	in.close();
    }

    protected abstract void parseTelnetOption(TelnetOption topt) 
    throws IOException;
}
