package org.cyberiantiger.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.io.IOException;

public abstract class AbstractChannelHandler implements ChannelHandler {
    
    protected SelectionKey key;
    
    protected IOThread thread;
    
    protected SelectableChannel chan;

    public AbstractChannelHandler(SelectableChannel chan,IOThread thread) 
        throws IOException {
        this.thread = thread;
        this.chan = chan;
        chan.configureBlocking(false);
    }

    public void accept() {
    }

    public void connect() throws IOException {
    }

    public void read() {
    }

    public void write() {
    }

    public void close() throws IOException {
        thread.deregister(this);
        if (chan.isOpen()) {
            chan.close();
        }
    }

    public SelectableChannel getSelectableChannel() {
        return chan;
    }
}
