package org.cyberiantiger.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.io.IOException;

public interface ChannelHandler {

    /**
     * Called by a IOThread when there are connections to accept on 
     * a ServerSocketChannel
     */
    public void accept();

    /**
     * Called by a IOThread to complete the connection sequence on
     * a tcp connection when the SocketChannel is ready.
     */
    public void connect() throws IOException;

    /**
     * Called by a IOThread when there are bytes available to be read
     * from the channel
     */
    public void readFromChannel();

    /**
     * Called by a IOThread when there is space in a channels write 
     * buffer for more bytes
     */
    public void writeToChannel() throws IOException;
    
    /**
     * Close the channel.
     */
    public void close() throws IOException;

    /**
     * Get the underlying SelectableChannel.
     */
    public SelectableChannel getSelectableChannel();
    
    /**
     * Set the SelectionKey to be utilized.
     * @param key
     */
    public void setSelectionKey(SelectionKey key);
    
    /**
     * The SelectionKey being used 
     */
    public SelectionKey getSelectionKey();
}
