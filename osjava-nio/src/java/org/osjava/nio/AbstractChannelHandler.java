package org.osjava.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.io.IOException;

/**
 * Abstract implementation of a ChannelHandler.  
 * 
 * @author Antony Riley
 * @version $Revision$
 */
public abstract class AbstractChannelHandler implements ChannelHandler {
    /**
     * The Selection key used by the ChannellHandler.  This key is the one that
     * is registered with the controlling {@link IOThread}
     */
    protected SelectionKey key;
    
    /**
     * The IOThread which listens to the activity on the SocketChannel.
     */
    protected IOThread thread;
    
    /**
     * The SocketChannel which wraps around the socket and passes the data
     * back and forth
     */
    protected SelectableChannel chan;

    /**
     * Creates a ChannelHandler. 
     * 
     * @param chan the Channel object representing the socket.
     * @param thread the controlling {@link IOThread}
     * @throws IOException if the channel cannot be made non-blocking.
     */
    public AbstractChannelHandler(SelectableChannel chan,IOThread thread) 
        throws IOException {
        this.thread = thread;
        this.chan = chan;
        chan.configureBlocking(false);
    }

    /**
     * @see org.osjava.nio.ChannelHandler#accept()
     */
    abstract public void accept();

    /**
     * @see org.osjava.nio.ChannelHandler#connect()
     * @throws IOException when the connection cannot be made.
     */
    abstract public void connect() throws IOException;

    /**
     * @see org.osjava.nio.ChannelHandler#readFromChannel()
     */
    abstract public void readFromChannel();

    /**
     * @see org.osjava.nio.ChannelHandler#writeToChannel()
     * @throws IOException if something prevents the data from being written to
     *         the SelectableChannel
     */
    abstract public void writeToChannel() throws IOException;

    /**
     * @see org.osjava.nio.ChannelHandler#close()
     * @throws IOException if something stops the channel from closing.
     */
    public void close() throws IOException {
        thread.deregister(this);
        if (chan.isOpen()) {
            chan.close();
        }
    }

    /**
     * @see org.osjava.nio.ChannelHandler#getSelectableChannel()
     * @return the SelectableChannel that the handler is handling.
     */
    public SelectableChannel getSelectableChannel() {
        return chan;
    }
}
