package org.cyberiantiger.telnet;

import java.io.*;
import java.net.*;

public class TelnetSocket {

    private TelnetSession session;
    private Socket connection;
    private OutputStream out;
    private InputStream in;
    private InputStreamReader reader;
    private OutputStreamWriter writer;

    public TelnetSocket(String dest,int port,TelnetSession session) 
    throws IOException 
    {
	this.session = session;
	connection = new Socket(dest,port);
	in = connection.getInputStream();
	out = connection.getOutputStream();
	reader = new InputStreamReader(new MyTelnetInputStream(in));
	writer = new OutputStreamWriter(out);
	session.setOutputStream(out);
    }

    public Reader getReader() {
	return reader;
    }

    public Writer getWriter() {
	return writer;
    }

    public void close() throws IOException {
	connection.close();
    }

    private class MyTelnetInputStream extends TelnetInputStream {
	public MyTelnetInputStream(InputStream in) {
	    super(in);
	}

	public void parseTelnetOption(TelnetOption topt) throws IOException {
	    if(session != null) {
		session.receiveOption(topt);
	    }
	}
    }
}
