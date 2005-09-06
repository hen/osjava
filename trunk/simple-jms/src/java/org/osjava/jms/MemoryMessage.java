package org.osjava.jms;
interface Message{
    public static final int DEFAULT_DELIVERY_MODE;
    public static final int DEFAULT_PRIORITY;
    public static final long DEFAULT_TIME_TO_LIVE;
    public String getJMSMessageID();
       throws JMSException
    public void setJMSMessageID(String);
       throws JMSException
    public long getJMSTimestamp();
       throws JMSException
    public void setJMSTimestamp(long);
       throws JMSException
    public byte[] getJMSCorrelationIDAsBytes();
       throws JMSException
    public void setJMSCorrelationIDAsBytes(byte[]);
       throws JMSException
    public void setJMSCorrelationID(String);
       throws JMSException
    public String getJMSCorrelationID();
       throws JMSException
    public Destination getJMSReplyTo();
       throws JMSException
    public void setJMSReplyTo(Destination);
       throws JMSException
    public Destination getJMSDestination();
       throws JMSException
    public void setJMSDestination(Destination);
       throws JMSException
    public int getJMSDeliveryMode();
       throws JMSException
    public void setJMSDeliveryMode(int);
       throws JMSException
    public boolean getJMSRedelivered();
       throws JMSException
    public void setJMSRedelivered(boolean);
       throws JMSException
    public String getJMSType();
       throws JMSException
    public void setJMSType(String);
       throws JMSException
    public long getJMSExpiration();
       throws JMSException
    public void setJMSExpiration(long);
       throws JMSException
    public int getJMSPriority();
       throws JMSException
    public void setJMSPriority(int);
       throws JMSException
    public void clearProperties();
       throws JMSException
    public boolean propertyExists(String);
       throws JMSException
    public boolean getBooleanProperty(String);
       throws JMSException
    public byte getByteProperty(String);
       throws JMSException
    public short getShortProperty(String);
       throws JMSException
    public int getIntProperty(String);
       throws JMSException
    public long getLongProperty(String);
       throws JMSException
    public float getFloatProperty(String);
       throws JMSException
    public double getDoubleProperty(String);
       throws JMSException
    public String getStringProperty(java.lang.String);
       throws JMSException
    public Object getObjectProperty(java.lang.String);
       throws JMSException
    public java.util.Enumeration getPropertyNames();
       throws JMSException
    public void setBooleanProperty(String,boolean);
       throws JMSException
    public void setByteProperty(String,byte);
       throws JMSException
    public void setShortProperty(String,short);
       throws JMSException
    public void setIntProperty(String,int);
       throws JMSException
    public void setLongProperty(String,long);
       throws JMSException
    public void setFloatProperty(String,float);
       throws JMSException
    public void setDoubleProperty(String,double);
       throws JMSException
    public void setStringProperty(String,java.lang.String);
       throws JMSException
    public void setObjectProperty(String,java.lang.Object);
       throws JMSException
    public void acknowledge();
       throws JMSException
    public void clearBody();
       throws JMSException
}

