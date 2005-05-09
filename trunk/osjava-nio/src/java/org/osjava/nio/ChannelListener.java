/*
 * org.osjava.nio.ChannelListener
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on May 1, 2005
 *
 * Copyright (c) 2004, Robert M. Zigweid All rights reserved.
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

import java.nio.ByteBuffer;

/**
 * Interface to be implemented by objects that want to listen for various 
 * events that happen to a SocketChannelHandler.
 * 
 * @author Robert M. Zigweid
 */
public interface ChannelListener {
    
    /**
     * Called when the {@link ChannelHandler} <code>serv</code> has accepted
     * the connection <code>con</code>.
     * 
     * @param serv the ChannelHandler that accepted the connection. 
     * @param con the ChannelHandler that was the connection accepted.
     */
    public void connectionAccepted(ChannelHandler serv, ChannelHandler con);
    
    /**
     * Called when the {@link ChannelHandler} <code>con</code> is closed.
     * 
     * @param con the ChannelHandler that was the connection accepted.
     */
    public void connectionClosed(ChannelHandler con);

    /**
     * Called when the {@link ChannelHandler} <code>con</code> has
     * succesfully established a connection.
     * 
     * @param con the ChannelHandler object that has successfully 
     *        established a connection.
     */
    public void connected(ChannelHandler con);

    /**
     * Called when the {@link ChannelHandler} <code>con</code> receives data.
     * The {@link ByteBuffer} <code>buf</code> contains the data that was 
     * received by the channel.
     *   
     * @param con the ChannelHandler which received the data. 
     * @param buf the data received.
     */
    public void receiveData(ChannelHandler con, ByteBuffer buf);
}
