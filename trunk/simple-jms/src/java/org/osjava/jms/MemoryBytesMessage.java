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

