package org.osjava.jms;

import javax.jms.Message;

import java.util.Enumeration;

public abstract class MemoryMessage implements Message {

    public String getJMSMessageID() throws JMSException {
    }

    public void setJMSMessageID(String id) throws JMSException {
    }

    public long getJMSTimestamp() throws JMSException {
    }

    public void setJMSTimestamp(long timestamp) throws JMSException {
    }

    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
    }

    public void setJMSCorrelationIDAsBytes(byte[] bytes) throws JMSException {
    }

    public void setJMSCorrelationID(String id) throws JMSException {
    }

    public String getJMSCorrelationID() throws JMSException {
    }

    public Destination getJMSReplyTo() throws JMSException {
    }

    public void setJMSReplyTo(Destination replyTo) throws JMSException {
    }

    public Destination getJMSDestination() throws JMSException {
    }

    public void setJMSDestination(Destination destination) throws JMSException {
    }

    public int getJMSDeliveryMode() throws JMSException {
    }

    public void setJMSDeliveryMode(int mode) throws JMSException {
    }

    public boolean getJMSRedelivered() throws JMSException {
    }

    public void setJMSRedelivered(boolean isRedelivered) throws JMSException {
    }

    public String getJMSType() throws JMSException {
    }

    public void setJMSType(String type) throws JMSException {
    }

    public long getJMSExpiration() throws JMSException {
    }

    public void setJMSExpiration(long expiration) throws JMSException {
    }

    public int getJMSPriority() throws JMSException {
    }

    public void setJMSPriority(int priority) throws JMSException {
    }

    public void clearProperties() throws JMSException {
    }

    public boolean propertyExists(String key) throws JMSException {
    }

    public boolean getBooleanProperty(String key) throws JMSException {
    }

    public byte getByteProperty(String key) throws JMSException {
    }

    public short getShortProperty(String key) throws JMSException {
    }

    public int getIntProperty(String key) throws JMSException {
    }

    public long getLongProperty(String key) throws JMSException {
    }

    public float getFloatProperty(String key) throws JMSException {
    }

    public double getDoubleProperty(String key) throws JMSException {
    }

    public String getStringProperty(String key) throws JMSException {
    }

    public Object getObjectProperty(String key) throws JMSException {
    }

    public Enumeration getPropertyNames() throws JMSException {
    }

    public void setBooleanProperty(String key, boolean bool) throws JMSException {
    }

    public void setByteProperty(String key, byte b) throws JMSException {
    }

    public void setShortProperty(String key, short s) throws JMSException {
    }

    public void setIntProperty(String key, int i) throws JMSException {
    }

    public void setLongProperty(String key, long ln) throws JMSException {
    }

    public void setFloatProperty(String key, float f) throws JMSException {
    }

    public void setDoubleProperty(String key, double d) throws JMSException {
    }

    public void setStringProperty(String key, String str) throws JMSException {
    }

    public void setObjectProperty(String key, Object object) throws JMSException {
    }

    public void acknowledge() throws JMSException {
    }

    public void clearBody() throws JMSException {
    }


}
