package org.cyberiantiger.nio;

import java.nio.ByteBuffer;

/**
 * An Interface to represent one end of a connection
 */
public interface Connection {

    /**
     * Set the other end of this connection
     */
    public void setConnection(Connection con);

    /**
     * Write bytes to this connection.
     *
     * This method <b>Must</b> read at least one byte from <code>bytes</code>.
     *
     * @param bytes A Buffer of
     */
    public void write(ByteBuffer bytes);

    /**
     * Called to indicate that this connection should be closed,
     * or has been closed.
     *
     * @param bytes Last array of bytes to write to the connection before
     * closing.
     */
    public void close(ByteBuffer bytes);
}
