/*
 * org.osjava.nio.IOUtils
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

import java.net.InetSocketAddress;
import java.net.ServerSocket;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Utility methods for the OSJava-NIO project.  This is the interface that
 * is most useful for people to utilize.  They are intended to make the
 * package easier to use without having to mess around with the inner workings
 * of the package.
 *
 * @author Anthony Riley and Robert M. Zigweid
 * @version $Rev$ $Date$

 */
public class IOUtils {

    /**
     * Create a {@link ServerSocketChannelHandler} that is wrapped around a
     * {@link ServerSocketChannel} listening to the address specified by
     * <code>addr</code>.  The handler is registered with the specified
     * {@link IOThread} before being returned.
     *
     * @param addr the address the ServerSocketChannel is bound to.
     * @param thread the IOThread to register the SocketChannel with.
     * @param acceptor The handler which is called when something connects
     *        to the newly established ServerSocketChannel.
     * @return a ServerSocketChannelHandler wrapped around the listening
     *         listening ServerSocketChannel object.
     * @throws ClosedChannelException if the underlying channel is closed.
     * @throws IOException if an IO exception occurs.
     */
    public static ServerSocketChannelHandler listen(InetSocketAddress addr,
                                                    IOThread thread,
                                                    SocketChannelHandlerAcceptor acceptor)
        throws ClosedChannelException, IOException {
        ServerSocketChannel chan = ServerSocketChannel.open();
        ServerSocketChannelHandler handler = new ServerSocketChannelHandler(chan,thread);
        int ops = chan.validOps();
        ServerSocket socket = chan.socket();
        socket.bind(addr);
        thread.register(handler, ops);
        handler.setSocketChannelHandlerAcceptor(acceptor);
        return handler;
        }

    /**
     * Create a {@link SocketChannelHandler} that is wrapped around a
     * {@link SocketChannel} listening to the address specified by
     * <code>addr</code>.  The handler is registered with the specified
     * {@link IOThread} before being returned.
     *
     * Note: There is no error handling here, if connectiong fails, I have
     * no clue what will happen, but this method will not throw an exception
     * as the underlying channel will be in a non-blocking mode, and hence
     * <code>connect()</code> will return immediately, and an error will be thrown
     * when .finishConnect() is invoked by the SocketChannelHandler
     *
     * @param addr the address the SocketChannel is bound to.
     * @param thread the IOThread to register the SocketChannel with.
     * @return a SocketChannelHandler wrapped around the listening
     *         listening ServerSocketChannel object.
     * @throws ClosedChannelException if the underlying channel is closed.
     * @throws IOException if an IO exception occurs.
     */
    public static SocketChannelHandler connect(InetSocketAddress addr,
                                               IOThread thread)
        throws ClosedChannelException, IOException {
        SocketChannel chan = SocketChannel.open();
        SocketChannelHandler handler = new SocketChannelHandler(chan,thread);
        int ops = chan.validOps();
        thread.register(handler, ops);
        chan.connect(addr);
        return handler;
    }
}
