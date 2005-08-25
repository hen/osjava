/*
 * org.osjava.nio.AbstractChannelHandler
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
     * The IOThread which listens to the activity on the SocketChannel.
     */
    protected IOThread thread;

    /**
     * The SocketChannel which wraps around the socket and passes the data
     * back and forth
     */
    protected SelectableChannel chan;

    /**
     * The ChannelListener for this handler
     */
    protected ChannelListener chanListener;
    
    /**
     * Creates a ChannelHandler for the channel <code>chan</code> to be
     * registered with the {@link IOThread} <code>thread</code>
     *
     * @param chan the Channel object representing the socket.
     * @param thread the controlling {@link IOThread} to which the
     *        SelectionKey will be registered.
     * @throws IOException if the channel cannot be made non-blocking.
     */
    public AbstractChannelHandler(SelectableChannel chan, IOThread thread)
        throws IOException {
        this.thread = thread;
        this.chan = chan;
        chan.configureBlocking(false);
    }

    /**
     * Close the underlying SelectableChannel.  The SelectionKey is also
     * deregistered from the controlling {@link IOThread}.
     *
     * @throws IOException if something prevents the channel from being closed.
     */
    public void close() throws IOException {
        thread.deregister(this);
        if (chan.isOpen()) {
            chan.close();
        }
        if(getChannelListener() != null) {
            getChannelListener().connectionClosed(this);
        }
    }

    /**
     * Returns the underlying SelectableChannel.
     *
     * @return the SelectableChannel that the handler is handling.
     */
    public SelectableChannel getSelectableChannel() {
        return chan;
    }

    /**
     * Return the controlling {@link IOThread} for the ChannelHandler.
     *
     * @return the controlling thread for the ChannelHandler.
     */
    public IOThread getThread() {
        return thread;
    }


    /**
     * Set the ChannelListener
     */
    public void setChannelListener(ChannelListener chanListener) {
        this.chanListener = chanListener;
    }

    /**
     * Get the ChannelListener
     */
    public ChannelListener getChannelListener() {
        return chanListener;
    }
}
