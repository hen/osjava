/*
 * org.osjava.nio.AbstractByteBroker
 *
 * $Id: ChannelHandler.java 1641 2005-04-23 20:23:14Z rzigweid $
 * $URL: https://svn.osjava.org/svn/osjava/trunk/osjava-nio/src/java/org/osjava/nio/ChannelHandler.java $
 * $Rev: 1641 $
 * $Date: 2005-04-23 21:23:14 +0100 (Sat, 23 Apr 2005) $
 * $Author: rzigweid $
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

/**
 * @author cybertiger
 */
public abstract class AbstractByteBroker implements ByteBroker {

    public int broker(byte[] data, boolean close) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        broker(buffer, close);
        return buffer.position();
    }

    public int broker(byte[] data, int offset, int len, boolean close) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        broker(buffer, close);
        return buffer.position() - offset;
    }

}
