package org.osjava.jms;
public class MemoryStreamMessage implements StreamMessage extends Message{
    public boolean readBoolean();
       throws JMSException
    public byte readByte();
       throws JMSException
    public short readShort();
       throws JMSException
    public char readChar();
       throws JMSException
    public int readInt();
       throws JMSException
    public long readLong();
       throws JMSException
    public float readFloat();
       throws JMSException
    public double readDouble();
       throws JMSException
    public String readString();
       throws JMSException
    public int readBytes(byte[]);
       throws JMSException
    public Object readObject();
       throws JMSException
    public void writeBoolean(boolean);
       throws JMSException
    public void writeByte(byte);
       throws JMSException
    public void writeShort(short);
       throws JMSException
    public void writeChar(char);
       throws JMSException
    public void writeInt(int);
       throws JMSException
    public void writeLong(long);
       throws JMSException
    public void writeFloat(float);
       throws JMSException
    public void writeDouble(double);
       throws JMSException
    public void writeString(String);
       throws JMSException
    public void writeBytes(byte[]);
       throws JMSException
    public void writeBytes(byte[],int,int);
       throws JMSException
    public void writeObject(Object);
       throws JMSException
    public void reset();
       throws JMSException
}

