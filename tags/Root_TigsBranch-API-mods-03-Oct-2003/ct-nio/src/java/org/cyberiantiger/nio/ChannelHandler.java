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
    public void connect();

    /**
     * Called by a IOThread when there are bytes available to be read
     * from the channel
     */
    public void read();

    /**
     * Called by a IOThread when there is space in a channels write 
     * buffer for more bytes
     */
    public void write();

    /**
     * Close the channel.
     */
    public void close() throws IOException;

    /**
     * Register this ChannelHandler with a IOThread.
     *
     * @Throws IllegalStateException for various reasons
     */
    public void register(IOThread thread);

    /**
     * Deregister this ChannelHandler
     */
    public void deregister();

    /**
     * Get the underlying SelectableChannel.
     */
    public SelectableChannel getSelectableChannel();
}
