package org.cyberiantiger.telnet;

import java.io.*;

/**
 * A buffered input stream which will not do a blocking read unless explictely
 * told to do so
 */
public class TelnetBufferedInputStream extends FilterInputStream {

    private byte[] buffer = new byte[2048];
    private int off = 0;
    private int len = 0;
    private boolean allowBlocking = false;

    public TelnetBufferedInputStream(InputStream in) {
	super(in);
    }

    public int read() throws IOException {
	if(allowBlocking && len == 0) {
	    fillBuffer();
	}
	if(len <= 0) {
	    return -1;
	} 
	len--;
	return buffer[off++] & 0xff;
    }

    public int read(byte[] b) throws IOException {
	return read(b, 0, b.length);
    }

    public int read(byte[] b, int bOff, int bLen) throws IOException {
	if(allowBlocking && len == 0) {
	    fillBuffer();
	}
	if(len <= 0) {
	    return -1;
	}
	if(len > bLen) {
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

    public void fillBuffer() throws IOException {
	if(len != 0) return;
	off = 0;
	len = in.read(buffer,off,buffer.length - len);
    }

    public int available() throws IOException {
	return len;
    }

    public void setAllowBlocking(boolean allowBlocking) {
	this.allowBlocking = allowBlocking;
    }
}
