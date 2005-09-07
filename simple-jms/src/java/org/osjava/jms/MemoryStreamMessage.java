package org.osjava.jms;

import javax.jms.StreamMessage;

// TODO:
/*  A value written as the row type can be read as the column type.

 |        | boolean byte short char int long float double String byte[]
 |----------------------------------------------------------------------
 |boolean |    X                                            X
 |byte    |          X     X         X   X                  X   
 |short   |                X         X   X                  X   
 |char    |                     X                           X
 |int     |                          X   X                  X   
 |long    |                              X                  X   
 |float   |                                    X     X      X   
 |double  |                                          X      X   
 |String  |    X     X     X         X   X     X     X      X   
 |byte[]  |                                                        X
 |----------------------------------------------------------------------
*/

public class MemoryStreamMessage extends MemoryMessage implements StreamMessage {

    public boolean readBoolean() throws JMSException {
    }

    public byte readByte() throws JMSException {
    }

    public short readShort() throws JMSException {
    }

    public char readChar() throws JMSException {
    }

    public int readInt() throws JMSException {
    }

    public long readLong() throws JMSException {
    }

    public float readFloat() throws JMSException {
    }

    public double readDouble() throws JMSException {
    }

    public String readString() throws JMSException {
    }

    public int readBytes(byte[] bytes) throws JMSException {
    }

    public Object readObject() throws JMSException {
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

    public void writeString(String str) throws JMSException {
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
