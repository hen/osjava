package org.osjava.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.io.IOException;

/**
 * Interface to a SelectableChannel Object.  The {@link IOThread} that controls
 * the channel makes many calls to this interface.
 * 
 * @author Antony Riley, Robert M. Zigweid
 * @version $Revision: 1.4 $ $Date$
 */
public interface ChannelHandler {

    /**
     * Called by a {@link IOThread} when there are connections to accept on 
     * a ServerSocketChannel
     */
    public void accept();

    /**
     * Called by a {@link IOThread} to complete the connection sequence on
     * a tcp connection when the SocketChannel is ready.
     * 
     * @throws IOException when the connection cannot be made.
     */
    public void connect() throws IOException;

    /**
     * Called by a {@link IOThread} when there are bytes available to be read
     * from the channel
     */
    public void readFromChannel();

    /**
     * Called by a {@link IOThread} when there is space in a channels write 
     * buffer for more bytes
     * 
     * @throws IOException if something prevents the data from being written to
     *         the SelectableChannel
     */
    public void writeToChannel() throws IOException;
    
    /**
     * Close the channel.
     * 
     * @throws IOException if something stops the channel from closing.
     */
    /* WTF would prevent close from being called properly? --Robert */
    public void close() throws IOException;

    /**
     * Get the underlying SelectableChannel.
     * 
     * @return the SelectableChannel that the handler is handling
     */
    public SelectableChannel getSelectableChannel();
    
    /**
     * Set the SelectionKey to be utilized.
     * @param key
     */
    public void setSelectionKey(SelectionKey key);
    
    /**
     * Returns the SelectionKey being used by the handler.
     * 
     * @return The SelectionKey being used by the handler.
     */
    public SelectionKey getSelectionKey();
}
