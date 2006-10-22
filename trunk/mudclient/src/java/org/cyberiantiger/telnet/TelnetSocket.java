package org.cyberiantiger.telnet;

import java.io.*;
import java.net.*;

public class TelnetSocket {

    private TelnetSession session;
    private Socket connection;
    private String encoding;
    private OutputStream out;
    private InputStream in;
    private InputStreamReader reader;
    private OutputStreamWriter writer;

    public TelnetSocket(Socket connection,TelnetSession session,String encoding) 
        throws IOException 
    {
	this.session = session;
	this.connection = connection;
        this.encoding = encoding;
	in = connection.getInputStream();
	out = connection.getOutputStream();
	reader = new InputStreamReader(new MyTelnetInputStream(in), encoding);
	writer = new OutputStreamWriter(out, encoding);
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
