/*
 * org.osjava.nio.ChannelHandler
 *
 * $Id$
 * $URL$
 * $Rev$
 * $Date$
 * $Author$
 *
 * Copyright (c) 2003-2005, Anthony Riley, Robert M. Zigweid
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * + Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * + Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * + Neither the name of the OSJava-NIO nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.nio;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.io.IOException;

/**
 * Interface to a SelectableChannel Object.  The {@link IOThread} that controls
 * the channel makes many calls to this interface.
 *
 * @author Antony Riley, Robert M. Zigweid
 * @version $Revision$ $Date$
 */
public interface ChannelHandler {

    /**
     * Called by a {@link IOThread} when there are connections to accept on
     * a ServerSocketChannel
     * @throws IOException 
     * @throws IllegalStateException 
     * @throws ClosedChannelException 
     */
    public void accept() throws ClosedChannelException, IllegalStateException, IOException;

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
    public void readFromChannel() throws IOException;

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
    public void close() throws IOException;

    /**
     * Get the underlying SelectableChannel.
     *
     * @return the SelectableChannel that the handler is handling
     */
    public SelectableChannel getSelectableChannel();

    /**
     * Get the thread that the handler is registered with.
     * @return the {@link IOThread} that the ChannelHandler is
     *         registered with.
     */
    public IOThread getThread();
    
    /**
     * Set the ChannelListener
     */
    public void setChannelListener(ChannelListener chanListener);

    /**
     * Get the ChannelListener
     */
    public ChannelListener getChannelListener();
}
