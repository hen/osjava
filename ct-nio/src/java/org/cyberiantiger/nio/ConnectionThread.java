package org.cyberiantiger.nio;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

public class ConnectionThread implements Runnable {

    private Selector mySelector;

    /**
     * Create a new ConnectionThread.
     *
     * @throws IOException If there is a problem creating the Selector
     */
    public ConnectionThread() throws IOException {
	mySelector = Selector.open();
    }

    /**
     * Listen to a TCP port.
     *
     * @param addr The InetSocketAddress to bind the server socket to.
     * @param factory The ConnectionFactory to use to create new Connection s.
     * @return The ServerSocket that was created and bound
     * @throws IOException When it feels like it.
     */
    public ServerSocket listen(
	    InetSocketAddress addr, 
	    ConnectionFactory factory) 
    throws IOException 
    {
	ServerSocketChannel ssc = ServerSocketChannel.open();
	ssc.configureBlocking(false);
	ServerSocket sock = ssc.socket();
	sock.bind(addr);
	ServerChannelHandler sch = new ServerChannelHandler(this,ssc,factory);
	add(ssc,sch,ssc.validOps());
	return sock;
    }

    void add(SelectableChannel chan, ChannelHandler handler, int ops) 
    throws IOException 
    {
	if(chan.isBlocking()) {
	    throw new IllegalArgumentException("SelectableChannel is blocking");
	}
	// We make the handler interested in all valid ops to start with.
	// It's up to the handler to change which operations it's interested
	// in, in future, it can do this, as it has a reference to the 
	// selectionkey
	// It can also deregister itself.
	handler.setSelectionKey(
		chan.register(mySelector,ops,handler)
		);
	synchronized(this) {
	    notify();
	}
    }

    public void run() {
	while(true) {
	    try {
		// Workaround, Selector.select(0) never returns if
		// there is nothing to select on ! (even if you 
		// call Selector.wakeup() )
		synchronized(this) {
		    try {
			while(mySelector.keys().size() == 0) wait();
		    } catch (InterruptedException ie) {
			return;
		    }
		}
		try {
		    while(mySelector.select() == 0);
		} catch (InterruptedIOException iie) {
		    return;
		}
		Set keys = mySelector.selectedKeys();
		Iterator i = keys.iterator();
		while(i.hasNext()) {
		    SelectionKey key = (SelectionKey) i.next();
		    ChannelHandler handler = (ChannelHandler) key.attachment();
		    if(key.isValid()) {
			int ops = key.readyOps();
			if( (ops & SelectionKey.OP_ACCEPT) != 0) {
			    handler.accept();
			}
			if( (ops & SelectionKey.OP_CONNECT) != 0) {
			    handler.connect();
			}
			if( (ops & SelectionKey.OP_READ) != 0) {
			    handler.read();
			}
			if( (ops & SelectionKey.OP_WRITE) != 0) {
			    handler.write();
			}
		    }
		    i.remove();
		}
	    } catch (IOException ioe) {
		ioe.printStackTrace();
	    }
	}
    }
}
