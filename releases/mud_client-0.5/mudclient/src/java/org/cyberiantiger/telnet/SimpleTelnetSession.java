package org.cyberiantiger.telnet;

import java.io.*;

public abstract class SimpleTelnetSession implements TelnetSession {

    private OutputStream out;

    String term;
    int width = 0;
    int height = 0;

    boolean nawsOn = false;
    boolean termOn = false;

    public SimpleTelnetSession(String term) {
	this.term = term;
    }

    public void receiveOption(TelnetOption opt) 
    throws IOException 
    {
	if(opt instanceof TelnetNegotiation) {
	    TelnetOption reply;
	    switch(opt.getOption()) {
		case TelnetOption.WILL:
		    switch(((TelnetNegotiation)opt).getSubOption()) {
			case TelnetOption.TOPT_ECHO:
			    serverWillEcho(true);
			    break;
			default:
			    break;
		    }
		    break;
		case TelnetOption.WONT:
		    switch(((TelnetNegotiation)opt).getSubOption()) {
			case TelnetOption.TOPT_ECHO:
			    serverWillEcho(false);
			    break;
			default:
			    break;
		    }
		    break;
		case TelnetOption.DO:
		    switch(((TelnetNegotiation)opt).getSubOption()) {
			case TelnetOption.TOPT_TERM:
			    reply = new TelnetNegotiation(
				    TelnetOption.WILL,
				    TelnetOption.TOPT_TERM
				    );
			    out.write(reply.getBytes());
			    termOn = true;
			    break;
			case TelnetOption.TOPT_NAWS:
			    reply = new TelnetNegotiation(
				    TelnetOption.WILL,
				    TelnetOption.TOPT_NAWS
				    );
			    out.write(reply.getBytes());
			    nawsOn = true;
			    sendWindowSize();
			    break;
			default:
			    // Ignored
			    break;
		    }
		    break;
		case TelnetOption.DONT:
		    break;
	    }
	} else if(opt instanceof TelnetSubOption) {
	    byte[] data;
	    data = ((TelnetSubOption)opt).getData();
	    switch(((TelnetSubOption)opt).getSubOption()) {
		case TelnetOption.TOPT_TERM:
		    if(data.length == 1 && data[0] == TelnetOption.SEND) {
			sendTerminalType();
		    }
		default:
		    break;
	    }
	}
    }

    public void setOutputStream(OutputStream out) throws IOException {
	this.out = out;
    }

    public void sendWindowSize() throws IOException {
	if(out != null && nawsOn && (width != 0 || height != 0) ) {
	    TelnetOption opt = new TelnetSubOption(
		    TelnetOption.TOPT_NAWS,
		    new byte[] {
			((byte) ((width>>8) & 0xff)),
			((byte) (width & 0xff)),
			((byte) ((height>>8) & 0xff)),
			((byte) (height & 0xff))
		    });
	    out.write(opt.getBytes());
	}
    }

    public void sendTerminalType() throws IOException {
	if(out != null && termOn) {
	    byte[] term = this.term.getBytes();
	    byte[] data = new byte[term.length+1];
	    data[0] = TelnetOption.IS;
	    System.arraycopy(term,0,data,1,term.length);
	    TelnetOption opt = new TelnetSubOption(
		    TelnetOption.TOPT_TERM,
		    data);
	    out.write(opt.getBytes());
	}
    }

    public void setWindowSize(int width, int height) throws IOException {
	if(this.width != width || this.height != height) {
	    this.width = width;
	    this.height = height;
	    sendWindowSize();
	}
    }

    protected abstract void serverWillEcho(boolean echo);
}
