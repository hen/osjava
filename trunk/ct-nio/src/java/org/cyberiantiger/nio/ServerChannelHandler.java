package org.cyberiantiger.nio;

import java.io.IOException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;

public class ServerChannelHandler implements ChannelHandler {
    private ConnectionThread thread;
    private ServerSocketChannel chan;
    private SelectionKey key;
    private ConnectionFactory factory;

    public ServerChannelHandler(
	    ConnectionThread thread,
	    ServerSocketChannel chan, 
	    ConnectionFactory factory
	    ) 
    {
	this.thread = thread;
	this.chan = chan;
	this.factory = factory;
    }

    public void read() {}
    public void write() {}
    public void connect() {}
    public void accept() {
	try {
	    SocketChannel sockChan = chan.accept();
	    if(sockChan != null) {
		Connection con = factory.newConnection(sockChan.socket());
		if(con == null) {
		    sockChan.close();
		} else {
		    SocketChannelHandler sch = new SocketChannelHandler(
			    thread,
			    sockChan,
			    con
			    );
		}
	    }
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
    }

    public void setSelectionKey(SelectionKey key) {
	this.key = key;
    }

    public void close() throws IOException {
	chan.close();
    }
}
