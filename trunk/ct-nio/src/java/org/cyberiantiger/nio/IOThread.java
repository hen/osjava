package org.cyberiantiger.nio;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

public class IOThread extends Thread {

    private Selector mySelector;

    /**
     * Create a new IOThread.
     *
     * @throws IOException If there is a problem creating the Selector
     */
    public IOThread() throws IOException {
	mySelector = Selector.open();
    }

    /**
     * Register a ChannelHandler with this IOThread
     *
     */
    SelectionKey register(
	    ChannelHandler handler, 
	    int ops) 
    throws IOException 
    {
	SelectableChannel chan = handler.getSelectableChannel();

	if(chan.isBlocking()) {
	    throw new IllegalArgumentException("SelectableChannel is blocking");
	}

	SelectionKey ret = chan.register(mySelector,ops,handler);

	// XXX: Workaround, Selector hangs when it has nothing registered.
	synchronized(this) {
	    notify();
	}
	return ret;
    }

    public void run() {
	while(true) {
	    try {
		// XXX: Workaround, Selector.select() never returns if
		// there is nothing to select on ! (even if you 
		// call Selector.wakeup() )
		synchronized(this) {
		    try {
			while(mySelector.keys().size() == 0) wait();
		    } catch (InterruptedException ie) {
			System.out.println("Breaking out of IOThread");
			break;
		    }
		}
		try {
		    while(mySelector.select() == 0);
		} catch (InterruptedIOException iie) {
		    System.out.println("Breaking out of IOThread");
		    break;
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
	// XXX: This doesn't work !!!!
	// Cleanup
	// This is where we cleanup the mess left when we're interupted
	// TODO: Only close channels if 'closeOnExit' is set.
	Iterator i = mySelector.keys().iterator();
	while(i.hasNext()) {
	    SelectionKey key = (SelectionKey) i.next();
	    ChannelHandler handler = (ChannelHandler) key.attachment();
	    if(key.isValid()) {
		try {
		    handler.close();
		    handler.deregister();
		} catch (IOException ioe) {
		    ioe.printStackTrace();
		}
	    }
	}
	try {
	    mySelector.close();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
    }
}
