package org.osjava.jms;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;

import javax.jms.MapMessage;
import javax.jms.JMSException;

public class MemoryMapMessage extends MemoryMessage implements MapMessage {

    private Map map = new HashMap();

    public boolean getBoolean(String key) throws JMSException {
        return ( (Boolean) this.map.get(key) ).booleanValue();
    }

    public byte getByte(String key) throws JMSException {
        return ( (Number) this.map.get(key) ).byteValue();
    }

    public short getShort(String key) throws JMSException {
        return ( (Number) this.map.get(key) ).shortValue();
    }

    public char getChar(String key) throws JMSException {
        return ( (Character) this.map.get(key) ).charValue();
    }

    public int getInt(String key) throws JMSException {
        return ( (Number) this.map.get(key) ).intValue();
    }

    public long getLong(String key) throws JMSException {
        return ( (Number) this.map.get(key) ).longValue();
    }

    public float getFloat(String key) throws JMSException {
        return ( (Number) this.map.get(key) ).floatValue();
    }

    public double getDouble(String key) throws JMSException {
        return ( (Number) this.map.get(key) ).doubleValue();
    }

    public String getString(String key) throws JMSException {
        return this.map.get(key).toString();
    }

    public byte[] getBytes(String key) throws JMSException {
        return (byte[]) this.map.get(key);
    }

    public Object getObject(String key) throws JMSException {
        return this.map.get(key);
    }

    public Enumeration getMapNames() throws JMSException {
        return Collections.enumeration(this.map.keySet());
    }

    public void setBoolean(String key, boolean bool) throws JMSException {
        this.map.put(key, new Boolean(bool));
    }

    public void setByte(String key, byte b) throws JMSException {
        this.map.put(key, new Byte(b));
    }

    public void setShort(String key, short s) throws JMSException {
        this.map.put(key, new Short(s));
    }

    public void setChar(String key, char c) throws JMSException {
        this.map.put(key, new Character(c));
    }

    public void setInt(String key, int i) throws JMSException {
        this.map.put(key, new Integer(i));
    }

    public void setLong(String key, long ln) throws JMSException {
        this.map.put(key, new Long(ln));
    }

    public void setFloat(String key, float f) throws JMSException {
        this.map.put(key, new Float(f));
    }

    public void setDouble(String key, double d) throws JMSException {
        this.map.put(key, new Double(d));
    }

    public void setString(String key, String str) throws JMSException {
        this.map.put(key, str);
    }

    public void setBytes(String key, byte[] bytes) throws JMSException {
        this.map.put(key, bytes);
    }

    public void setBytes(String key, byte[] bytes, int offset, int length) throws JMSException {
        byte[] array = new byte[length];
        System.arraycopy(bytes, offset, array, 0, length);
        this.map.put(key, array);
    }

    public void setObject(String key, Object object) throws JMSException {
        this.map.put(key, object);
    }

    public boolean itemExists(String key) throws JMSException {
        return this.map.containsKey(key);
    }


}

