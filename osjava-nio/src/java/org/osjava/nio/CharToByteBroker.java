/*
 * org.osjava.nio.CharToByteBroker
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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

/**
 * @author cybertiger
 *
 */
public class CharToByteBroker extends AbstractCharBroker {

    private ByteBroker aBroker;

    private CharsetEncoder encoder;

    private ByteBuffer encodeBuffer;

    public CharToByteBroker(ByteBroker aBroker) {
        this(aBroker, Charset.forName("UTF-8").newEncoder());
    }

    public CharToByteBroker(ByteBroker aBroker, CharsetEncoder encoder) {
        this(aBroker, encoder, 1024);
    }

    public CharToByteBroker(
            ByteBroker aBroker, CharsetEncoder encoder, int bufSize
            ) 
    {
        this.aBroker = aBroker;
        this.encoder = encoder;
        this.encodeBuffer = ByteBuffer.allocate(bufSize);
    }

    /* 
     * This is where it actually happens, rest of the methods just call this
     */
    public void broker(CharBuffer data, boolean close) {
        while(true) {
            CoderResult result = encoder.encode(data, encodeBuffer, close);
            /* 
             * If we have any data, try to send it.
             */
            if(encodeBuffer.position() > 0) {
                encodeBuffer.flip();
                if( data.hasRemaining() ) {
                    aBroker.broker(encodeBuffer, false);
                } else {
                    aBroker.broker(encodeBuffer, close);
                }
                encodeBuffer.compact();
            }
            if(encodeBuffer.hasRemaining()) {
                /* 
                 * Whilst we don't need more data, whoever we're
                 * passing data onto doesn't seem to want any more
                 * data (yet)
                 */
                return;
            }
            if(result.isUnderflow()) {
                /*
                 * We need more data to continue, return 
                 */
                return;
            }
        }
    }

    public boolean isClosed() {
        return aBroker.isClosed();
    }
}
