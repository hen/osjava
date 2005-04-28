/*
 * org.osjava.nio.SingleTest
 * $Id: SingleThreadTest.java 1651 2005-04-24 21:21:02Z rzigweid $
 * $Rev: 1651 $ 
 * $Date: 2005-04-24 22:21:02 +0100 (Sun, 24 Apr 2005) $ 
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

import java.util.Random;

public class ThreadTest {

    private final static Random rnd = new Random();

    /*
     * Simple echo server
     * Echo data send byte for byte.
     */
    public static class ByteEchoServer 
        extends AbstractSocketChannelHandlerAcceptor 
    {
        public ByteEchoServer(IOThread iot) {
            super(iot);
        }

        public void acceptSocketChannelHandler(SocketChannelHandler sch) 
            throws IOException 
        {
            super.acceptSocketChannelHandler(sch);
            /*
             * Simply write to self
             */
            sch.setByteBroker(sch);
        }

    }

    /*
     * A slightly more thorough version of the ByteServer
     * which also includes the byte to char and char to byte convertors.
     *
     * FIXME: Right now this will probably break hideously as
     * invalid characters are handled VERY badly in the converters.
     */
    public static class CharEchoServer 
        extends AbstractSocketChannelHandlerAcceptor
    {
        public CharEchoServer(IOThread iot) {
            super(iot);
        }

        public void acceptSocketChannelHandler(SocketChannelHandler sch) 
            throws IOException 
        {
            super.acceptSocketChannelHandler(sch);
            /*
             * Write to self via byte -> char and char -> byte 
             * converters
             */
            sch.setByteBroker(
                    new ByteToCharBroker(
                        new CharToByteBroker(sch)
                        )
                    );
        }
    }
    
    /*
     * Send out data, verify incoming data, in theory we're going
     * to connect this to the echo server and hopefully get
     * the same data back
     */
    public static class DataIntegrityTestBroker extends AbstractByteBroker 
    {
        private ByteBroker out;
        private int outCount;
        private int inCount;
        private ByteBuffer outBuffer = ByteBuffer.allocate(1024);
        private Random outGenerator;
        private Random inGenerator;

        public DataIntegrityTestBroker(ByteBroker out, int byteCount) {
            this.out = out;
            this.inCount = byteCount;
            this.outCount = byteCount;
            /*
             * Use a couple of randomly seeded random number generators
             * one of which generates data, and one of which checks
             * incoming data
             */
            long rndSeed = rnd.nextLong();
            outGenerator = new Random(rndSeed);
            inGenerator = new Random(rndSeed);
            writeToOutput();
        }

        /*
         * drive as much data down our output as possible
         */
        private void writeToOutput() {
            if(outBuffer.position() == 0 && outCount == 0) {
                /*
                 * We've finished already
                 */
                return;
            }
            while(true) {
                byte[] foo = new byte[1];
                while(outBuffer.hasRemaining() && outCount > 0)  {
                    outGenerator.nextBytes(foo);
                    outBuffer.put(foo[0]);
                    outCount--;
                }
                
                outBuffer.flip();
                out.broker(outBuffer, outCount == 0);
                outBuffer.compact();

                /*
                 * if our outBuffer still has data
                 * or if we're no longer filling the outBuffer with
                 * data, return, as whatever we're writing to has
                 * had its' fill
                 */
                if(!outBuffer.hasRemaining() || outCount == 0) 
                    return;
            }
        }

        /*
         * incoming data
         */
        public void broker(ByteBuffer data, boolean close) {
            while(data.hasRemaining() && inCount > 0) {
                byte b = data.get();
                byte[] foo = new byte[1];
                inGenerator.nextBytes(foo);
                if(b != foo[0]) {
                    System.out.println("Data integrity test failed "+
                            "- corrupt data");
                    System.exit(1);
                }
                inCount--;
            }
            if(inCount == 0 && data.hasRemaining()) {
                System.out.println(data);
                System.out.println("Data integrity test failed "+
                        "- received more data than we sent");
                System.exit(1);
            }
            if(close && inCount >0) {
                System.out.println("Data integrity test failed "+
                        "- stream closed before end of expected data");
            }

            if(inCount == 0) {
                System.out.println("Data test passed");
            }
            /*
             * finally, try to send some more data
             */
            writeToOutput();
        }
    }

    public static void main(String[] args) throws Exception {
        if(args.length != 2) {
            System.out.println("Usage: <no. of connections> <bytes to send>");
            return;
        }
        int connectionCount = Integer.parseInt(args[0]);
        int byteCount = Integer.parseInt(args[1]);

        IOThread iot = new IOThread();
        iot.start();
        ByteEchoServer echoHandler = new ByteEchoServer(iot);
        IOUtils.listen(new InetSocketAddress(9999), iot, echoHandler);

        for(int i=0;i<connectionCount;i++) {
            SocketChannelHandler sch = IOUtils.connect(
                    new InetSocketAddress("localhost",9999),
                    iot);
            sch.setByteBroker( new DataIntegrityTestBroker(sch, byteCount) );
        }
    }
}
