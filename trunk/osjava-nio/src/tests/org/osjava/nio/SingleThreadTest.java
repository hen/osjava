/*
 * org.osjava.nio.SingleThreadTest
 * $Id$
 * $Rev$ 
 * $Date$ 
 * $Author$
 * $URL$
 * 
 * Created on Apr 24, 2005
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

import java.io.BufferedWriter;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import junit.framework.TestCase;

public class SingleThreadTest extends TestCase {
    class EchoServer extends AbstractSocketChannelHandlerAcceptor implements SocketListener {
        public EchoServer(IOThread iot) {
            super(iot);
        }

        public void receiveData(ByteBuffer buf) {
            CharBuffer charBuffer = null;
            
            ByteBuffer outBuf = buf.duplicate();
            /* Create the echo to test the sending mechanism */
            /* This is of course, more complicated than it needs to be. */
            handler.write("You typed: '");          // Write a String
            handler.write(outBuf);                  // Write a ByteBuffer
            handler.write("'\n");                   // Write a String
        }
    }
    
    ServerSocketChannelHandler handler = null;
    SocketChannelHandlerAcceptor echoServer = null;
    IOThread iot = null;

    protected void setUp() throws Exception {
        super.setUp();
        /* Set up the listening echo server */
        iot = new IOThread();
        iot.start();
        acceptor = new AbstractSocketChannelHandlerAcceptor(iot) implements SocketListener {
            public void acceptSocketChannelHandler(SocketChannelHandler sch) throws IOException {
                super.acceptSocketChannelHandler(sch);
                sch.addListener(this);
                System.out.println("Accepting connection");
            };
            
        };
        handler = IOUtils.listen(new InetSocketAddress("localhost",9999), iot, acceptor);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        /* Close down the listening port, we are done. */
        handler.close();
    }
    
    public void testConnect() throws ClosedChannelException, IOException {
        SocketChannelHandler sock = IOUtils.connect(
                        new InetSocketAddress("localhost",9999),
                        iot);
    }
    
    public void testSend() {
        SocketChannelHandler sock = IOUtils.connect(
                        new InetSocketAddress("localhost",9999),
                        iot);
        sock.write("hello");
        sock.close();
    }
    
    public void testSendReceive() {
        String testString = "Hello";
        StringBuffer outString = new StringBuffer();
        SocketChannelHandler sock = IOUtils.connect(
                        new InetSocketAddress("localhost",9999),
                        iot);
        sock.write(testString);
        
        /* I'm not sure I like this, but I want the construct to be able to
         * take various types.  All methods of the same name must return the 
         * same type.  Passing by reference is the only solution, and Strings
         * are immutable */
        sock.read(outString);
        sock.close();
    }
}
