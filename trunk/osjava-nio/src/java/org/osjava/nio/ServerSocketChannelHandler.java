/*
 * org.osjava.nio.ServerSocketChannelHandler
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

import java.net.ServerSocket;

import java.nio.channels.ClosedChannelException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SelectableChannel;

import java.util.Iterator;

public class ServerSocketChannelHandler
    extends AbstractChannelHandler {

    private ServerSocketChannel chan;
    
    
    private SelectionKey key = null;

    private IOThread sockThread = null;

    public ServerSocketChannelHandler(ServerSocketChannel chan,IOThread thread)
        throws IOException {
            super(chan,thread);
            this.chan = chan;

            sockThread = thread;
        }

    /**
     * A new connection has been initiated. Register it with the a new
     * SocketChannel handler, and the thread handling this ServerSocketChannel.
     */
    public void accept() throws 
        ClosedChannelException, IllegalStateException, IOException {
        SocketChannel sockChan = null;
        
        try {
            sockChan = chan.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        if (sockChan != null) {
            int ops = sockChan.validOps() & ~SelectionKey.OP_CONNECT ;
            // Create a new SocketChannelHandler
            SocketChannelHandler sch =  new SocketChannelHandler(sockChan, sockThread);
            /* Register the Handler with the IOThread. */
            sockThread.register(sch, ops);
            sockThread.addInterestOp(sch, SelectionKey.OP_READ);
            /* Make sure that the connection op is removed */
            sockThread.removeInterestOp(sch, SelectionKey.OP_CONNECT);
            /* Notify Connection Listeners*/
            Iterator it = getChannelListeners().iterator();
            while(it.hasNext()) {
                ((ChannelListener)it.next()).connectionAccepted(this, sch);
            }
        }        
    }
    
    public void close() throws IOException {
        chan.close();
        /* Notify Connection Listeners*/
        Iterator it = getChannelListeners().iterator();
        while(it.hasNext()) {
            ((ChannelListener)it.next()).connectionClosed(this);
        }
    }

    public SelectableChannel getSelectableChannel() {
        return chan;
    }

    public ServerSocket getServerSocket() {
        return chan.socket();
    }

    /*
     * (non-Javadoc) This is a no-op on ServerSocketChannelHandler atm, but
     * @see org.cyberiantiger.nio.ChannelHandler#connect()
     */
    public void connect() {
        /*
         * TODO: Add IllegalStateException to throw. ServerSockets don't
         * connect, they listen.
         */
    }

    /* (non-Javadoc)
     * @see org.cyberiantiger.nio.ChannelHandler#readFromChannel()
     */
    public void readFromChannel() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.cyberiantiger.nio.ChannelHandler#writeToChannel()
     */
    public void writeToChannel() {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see org.cyberiantiger.nio.ChannelHandler#getSelectionKey()
     */
    public SelectionKey getSelectionKey() {
        return key;
    }

    /* (non-Javadoc)
     * @see org.cyberiantiger.nio.ChannelHandler#setSelectionKey(java.nio.channels.SelectionKey)
     */
    public void setSelectionKey(SelectionKey key) {
        this.key = key;

    }

    public void setByteBroker(ByteBroker aBroker) {
        /* Not applicable */
    }
}
