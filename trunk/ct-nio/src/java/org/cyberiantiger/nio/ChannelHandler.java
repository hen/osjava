package org.cyberiantiger.nio;

import java.nio.channels.SelectionKey;
import java.io.IOException;

public interface ChannelHandler {

    /**
     * Called by a ConnectionThread when there are connections to accept on 
     * a ServerSocketChannel
     */
    public void accept();

    /**
     * Called by a ConnectionThread to complete the connection sequence on
     * a tcp connection when the SocketChannel is ready.
     */
    public void connect();

    /**
     * Called by a ConnectionThread when there are bytes available to be read
     * from the channel
     */
    public void read();

    /**
     * Called by a ConnectionThread when there is space in a channels write 
     * buffer for more bytes
     */
    public void write();

    /**
     * Set the SelectionKey for this handler.
     * This is called by the ConnectionThread.
     */
    public void setSelectionKey(SelectionKey key);

    /**
     * Close a channel
     */
    public void close() throws IOException;

}
