package org.cyberiantiger.mudclient.net;

import java.io.*;
import java.net.*;
import org.cyberiantiger.telnet.*;
import org.cyberiantiger.mudclient.parser.Parser;

public class MudConnection extends Thread {

    public static final int DISCONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;
    public static final int DISCONNECTING = 3;

    private int status = DISCONNECTED;
    private String destination;
    private int port;
    private TelnetSocket sock;
    private SimpleTelnetSession session;
    private Display client;
    private Parser parser;
    
    /**
     * Create a new MudConnection for the specified MudClient to the
     * destination:port.
     */
    public MudConnection(Display client) {
	this.client = client;
	this.destination = client.getConfiguration().getHost();
	this.port = client.getConfiguration().getPort();
	start();
    }

    /**
     * Get the current status of the MudConnection.
     * TODO: Make this watchable, so that listeners can recieve notification
     * when the state changes.
     */
    public int getStatus() {
	return status;
    }

    /**
     * Connect to the mud.
     * @return True on a sucessful connection, false otherwise.
     */
    public synchronized boolean connect() {
	if(status == CONNECTED) return true;
	status = CONNECTING;
	client.connectionStatusChanged(CONNECTING);
	session = new SimpleTelnetSession(
		client.getConfiguration().getTerminalType()
		) {
	    protected void serverWillEcho(boolean echo) {
		client.connectionDoLocalEcho(!echo);
	    }
	};
	try {
	    sock = new TelnetSocket(destination, port, session);
	    status = CONNECTED;
	    client.connectionStatusChanged(CONNECTED);
	    notify();
	    return true;
	} catch(IOException ioe) {
	    ioe.printStackTrace();
	}
	status = DISCONNECTED;
	client.connectionStatusChanged(DISCONNECTED);
	return false;
    }

    /**
     * Disconnect from the mud.
     * @return True on sucessful disconnection.
     */
    public synchronized boolean disconnect() {
	status = DISCONNECTING;
	client.connectionStatusChanged(DISCONNECTING);
	if(sock != null) {
	    try {
		sock.close();
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
	sock = null;
	status = DISCONNECTED;
	client.connectionStatusChanged(DISCONNECTED);
	return true;
    }

    /**
     * Set the window size which is reported to the mud via telnet negotiation.
     */
    public void setWindowSize(int width, int height) {
	if(session != null) {
	    try {
		session.setWindowSize(width,height);
	    } catch (IOException ioe) {
		disconnect();
	    }
	}
    }

    /**
     * Get whether or not we should echo user input to the console.
     */
    public boolean getLocalEcho() {
	return false;
    }

    /**
     * Set the parser to use to parse output from the mud.
     */
    public void setParser(Parser parser) {
	if(this.parser != null) {
	    synchronized(this.parser) {
		parser.flush(client.getConsoleWriter());
		this.parser = parser;
	    }
	} else {
	    this.parser = parser;
	}
    }

    public void command(String text) {
	Writer out = sock.getWriter();
	if(out != null) {
	    try {
		out.write(text+'\n');
		out.flush();
	    } catch (IOException ioe) {
		ioe.printStackTrace();
		disconnect();
	    }
	}
    }

    /**
     * Read bytes from the TelnetSocket [if it's connected], parse, and 
     * deliver the appropriate ConsoleActions to the MudClient.
     */
    public void run() {
	while(true) {
	    while(status != CONNECTED) {
		synchronized(this) {
		    try {
			wait();
		    } catch(InterruptedException ie) {
			break;
		    }

		}
	    }
	    try {
		while(status == CONNECTED) {
		    int ch = sock.getReader().read();
		    if(ch == -1) {
			disconnect();
		    } else {
			if(parser != null) {
			    synchronized(parser) {
				parser.putChar((char)ch);
				if(parser.changeParser()) {
				    setParser(parser.getNewParser());
				} else if(!sock.getReader().ready()) {
				    parser.flush(client.getConsoleWriter());
				}
			    }
			}
		    }
		}
	    } catch (Exception e) {
		disconnect();
		e.printStackTrace();
	    }
	}
    }
}
