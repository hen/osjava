package org.cyberiantiger.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.io.IOException;

public abstract class AbstractChannelHandler implements ChannelHandler {

    protected SelectionKey key;
    
    public void accept() {
    }

    public void connect() {
    }

    public void read() {
    }

    public void write() {
    }

    public void close() throws IOException {
    }

    public void register(IOThread thread) {
	if(key != null) {
	    throw 
	    new IllegalStateException("ChannelHandler already registered");
	}
	SelectableChannel chan = getSelectableChannel();
	int ops = chan.validOps();
	// If we're already connected, we're not interested in the 
	// connect op.
	if(chan.isOpen()) {
	    ops &= ~ SelectionKey.OP_CONNECT;
	}
	// Feckski Offski, I hate the way *everything* throws IOException
	// It is *NOT* an IOException if you try and register a closed 
	// channel, it is an IllegalStateException.
	//
	// Fuckwits.
	try {
	    key = thread.register(this, ops);
	} catch (IOException ioe) {
	    throw new IllegalStateException("Underlying Channel is closed");
	}
    }

    public void deregister() {
	if(key != null) {
	    key.cancel();
	    key = null;
	}
    }

    public abstract SelectableChannel getSelectableChannel();

}
