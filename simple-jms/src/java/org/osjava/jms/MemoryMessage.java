package org.osjava.jms;

import java.util.Enumeration;
import java.util.Collections;

import javax.jms.Message;

public abstract class MemoryMessage implements Message {

    private String jmsMessageId;
    private long jmsTimestamp;
    private String jmsCorrelationId;
    private Destination jmsReplyTo;
    private int jmsDeliveryMode;
    private boolean jmsRedelivered;
    private String jmsType;
    private long jmsExpiration;
    private int jmsPriority;
    private HashMap properties = new HashMap();

    public String getJMSMessageID() throws JMSException {
        return this.jmsMessageId;
    }

    public void setJMSMessageID(String id) throws JMSException {
        this.jmsMessageId = id;
    }

    public long getJMSTimestamp() throws JMSException {
        return this.jmsTimestamp;
    }

    public void setJMSTimestamp(long timestamp) throws JMSException {
        this.jmsTimestamp = timestamp;
    }

    public byte[] getJMSCorrelationIDAsBytes() throws JMSException {
        return this.jmsCorrelationId.getBytes();
    }

    public void setJMSCorrelationIDAsBytes(byte[] bytes) throws JMSException {
        this.jmsMessageId = new String(bytes);
    }

    public void setJMSCorrelationID(String id) throws JMSException {
        this.jmsCorrelationId = id;
    }

    public String getJMSCorrelationID() throws JMSException {
        return this.jmsCorrelationId;
    }

    public Destination getJMSReplyTo() throws JMSException {
        return this.jmsReplyTo;
    }

    public void setJMSReplyTo(Destination replyTo) throws JMSException {
        this.jmsReplyTo = replyTo;
    }

    public Destination getJMSDestination() throws JMSException {
        return this.jmsDestination;
    }

    public void setJMSDestination(Destination destination) throws JMSException {
        this.jmsDestination = destination;
    }

    public int getJMSDeliveryMode() throws JMSException {
        return this.jmsDeliveryMode;
    }

    public void setJMSDeliveryMode(int mode) throws JMSException {
        this.jmsDeliveryMode = mode;
    }

    public boolean getJMSRedelivered() throws JMSException {
        return this.jmsRedelivered;
    }

    public void setJMSRedelivered(boolean isRedelivered) throws JMSException {
        this.jmsRedelivered = isRedelivered;
    }

    public String getJMSType() throws JMSException {
        return this.jmsType;
    }

    public void setJMSType(String type) throws JMSException {
        this.jmsType = type;
    }

    public long getJMSExpiration() throws JMSException {
        return this.jmsExpiration;
    }

    public void setJMSExpiration(long expiration) throws JMSException {
        this.jmsExpiration = expiration;
    }

    public int getJMSPriority() throws JMSException {
        return this.jmsPriority;
    }

    public void setJMSPriority(int priority) throws JMSException {
        this.jmsPriority = priority;
    }

    public void clearProperties() throws JMSException {
        this.properties.clear();
    }

    public boolean propertyExists(String key) throws JMSException {
        return this.properties.containsKey(key);
    }

    // TODO: Can't remember if properties are meant to share the 
    // same space or not. ie) can I have both a String and int "age" 
    // property that have different values?
    public boolean getBooleanProperty(String key) throws JMSException {
        return ( (Boolean) this.properties.get(key) ).booleanValue();
    }

    public byte getByteProperty(String key) throws JMSException {
        return ( (Number) this.properties.get(key) ).byteValue();
    }

    public short getShortProperty(String key) throws JMSException {
        return ( (Number) this.properties.get(key) ).shortValue();
    }

    public int getIntProperty(String key) throws JMSException {
        return ( (Number) this.properties.get(key) ).intValue();
    }

    public long getLongProperty(String key) throws JMSException {
        return ( (Number) this.properties.get(key) ).longValue();
    }

    public float getFloatProperty(String key) throws JMSException {
        return ( (Number) this.properties.get(key) ).floatValue();
    }

    public double getDoubleProperty(String key) throws JMSException {
        return ( (Number) this.properties.get(key) ).doubleValue();
    }

    public String getStringProperty(String key) throws JMSException {
        return this.properties.get(key).toString();
    }

    public Object getObjectProperty(String key) throws JMSException {
        return this.properties.get(key);
    }

    public Enumeration getPropertyNames() throws JMSException {
        return Collections.enumeration(this.properties.keySet());
    }

    public void setBooleanProperty(String key, boolean bool) throws JMSException {
        this.properties(key, new Boolean(bool));
    }

    public void setByteProperty(String key, byte b) throws JMSException {
        this.properties(key, new Byte(b));
    }

    public void setShortProperty(String key, short s) throws JMSException {
        this.properties(key, new Short(s));
    }

    public void setIntProperty(String key, int i) throws JMSException {
        this.properties(key, new Integer(i));
    }

    public void setLongProperty(String key, long ln) throws JMSException {
        this.properties(key, new Long(ln));
    }

    public void setFloatProperty(String key, float f) throws JMSException {
        this.properties(key, new Float(f));
    }

    public void setDoubleProperty(String key, double d) throws JMSException {
        this.properties(key, new Double(d));
    }

    public void setStringProperty(String key, String str) throws JMSException {
        this.properties(key, str);
    }

    public void setObjectProperty(String key, Object object) throws JMSException {
        this.properties(key, object);
    }

    public void acknowledge() throws JMSException {
        // TODO: acknowledge()?
    }

    public void clearBody() throws JMSException {
        // TODO: clearBody()?
    }


}
