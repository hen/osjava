package org.cyberiantiger.nio;

import java.nio.ByteBuffer;

/**
 * An interface to represent a simple data output.
 */
public interface Output {

    /**
     * Write the specified bytes to this Output.
     *
     * This method may read none, some or all of the bytes from the buffer
     * passed as an argument.
     *
     * The caller must handle any bytes which are not written.
     * @param bytes the bytes to write to this Output
     */
    public void write(ByteBuffer bytes);

    /**
     * Close this output.
     * 
     * Write bytes (and any buffered bytes still waiting to be written
     * to the underlying output stream), and then close this Output.
     *
     * This method may read none, some or all of the bytes from the buffer
     * passed as an argument.
     *
     * Note: This method may or may not close the underlying output immediately.
     *
     * @param bytes the data to write to this Output before closing it.
     */
    public void close(ByteBuffer bytes);

}
