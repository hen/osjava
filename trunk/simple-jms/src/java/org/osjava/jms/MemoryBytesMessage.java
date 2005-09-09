/*
 * Copyright (c) 2005, Steve Heath, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of OSJava nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
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
package org.osjava.jms;

import javax.jms.BytesMessage;
import javax.jms.JMSException;

public class MemoryBytesMessage extends MemoryMessage implements BytesMessage {

    public long getBodyLength() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public boolean readBoolean() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public byte readByte() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public int readUnsignedByte() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public short readShort() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public int readUnsignedShort() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public char readChar() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public int readInt() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public long readLong() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public float readFloat() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public double readDouble() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public String readUTF() throws JMSException {
        throw new RuntimeException("TODO");
    }

    public int readBytes(byte[] bytes) throws JMSException {
        throw new RuntimeException("TODO");
    }

    public int readBytes(byte[] bytes, int length) throws JMSException {
        throw new RuntimeException("TODO");
    }

    public void writeBoolean(boolean bool) throws JMSException {
    }

    public void writeByte(byte b) throws JMSException {
    }

    public void writeShort(short s) throws JMSException {
    }

    public void writeChar(char c) throws JMSException {
    }

    public void writeInt(int i) throws JMSException {
    }

    public void writeLong(long ln) throws JMSException {
    }

    public void writeFloat(float f) throws JMSException {
    }

    public void writeDouble(double d) throws JMSException {
    }

    public void writeUTF(String utf) throws JMSException {
    }

    public void writeBytes(byte[] bytes) throws JMSException {
    }

    public void writeBytes(byte[] bytes, int offset, int length) throws JMSException {
    }

    public void writeObject(Object object) throws JMSException {
    }

    public void reset() throws JMSException {
    }


}

