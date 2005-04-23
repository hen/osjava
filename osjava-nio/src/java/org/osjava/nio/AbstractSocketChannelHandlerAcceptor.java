/*
 * org.osjava.nio.AbstractSocketChannelHandler
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

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SelectionKey;

/**
 * Abstract implemenation of a SocketChannelHandlerAcceptor.
 *
 * @author Anthony Riley and Robert M. Zigweid
 * @version $Rev$ $Date$
 */
public abstract class AbstractSocketChannelHandlerAcceptor
implements SocketChannelHandlerAcceptor
{

    /**
     * The thread which controls the Acceptor.
     */
    private IOThread myThread;

    /**
     * Creates a new AbstractSocketChannelHandlerAcceptor controlled by
     * the {@link IOThread} <code>thread</code>.
     *
     * @param myThread the IOThread controlling the acceptor.
     */
    public AbstractSocketChannelHandlerAcceptor(IOThread myThread) {
        this.myThread = myThread;
    }

    /**
     * Accept a {@link SocketChannelHandler}.  This method is called when a
     * {@link ServerSocketChannelHandler} accepts a new connection.  This
     * method sets up the interest ops for the new Socket that will be used
     * for further communication with the client.  If the underlying
     * socket cannot accept the connection, the ChannelHandler
     * is closed.
     *
     * @param sch the SocketChannelHandler which is to have its interest ops
     *        changed.
     * @throws IOException if the SocketChannelHandler cannot be closed when
     *         the attempt is made.
     */
    public void acceptSocketChannelHandler(SocketChannelHandler sch)
        throws IOException {
        /* Register the reading op with the channel. */
        myThread.addInterestOp(sch.getSelectionKey(), SelectionKey.OP_READ);
        if(!acceptConnection(sch.getSocket())) {
            sch.close();
        }
    }

    /**
     * Returns true of the Socket can be accepted.
     *
     * @param con the Socket to accept.
     * @return true if the Socket can be accepted, otherwise false.
     */
    protected boolean acceptConnection(Socket con) {
        return true;
    }
}
