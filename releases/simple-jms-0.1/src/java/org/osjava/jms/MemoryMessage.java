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

import java.util.Enumeration;
import java.util.Collections;
import java.util.HashMap;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;

public class MemoryMessage implements Message {

    private String jmsMessageId;
    private long jmsTimestamp;
    private String jmsCorrelationId;
    private Destination jmsReplyTo;
    private Destination jmsDestination;
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
        if(id == null || !id.startsWith("ID:")) {
            throw new JMSException("ID must start with the text 'ID:'");
        }
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
        this.properties.put(key, new Boolean(bool));
    }

    public void setByteProperty(String key, byte b) throws JMSException {
        this.properties.put(key, new Byte(b));
    }

    public void setShortProperty(String key, short s) throws JMSException {
        this.properties.put(key, new Short(s));
    }

    public void setIntProperty(String key, int i) throws JMSException {
        this.properties.put(key, new Integer(i));
    }

    public void setLongProperty(String key, long ln) throws JMSException {
        this.properties.put(key, new Long(ln));
    }

    public void setFloatProperty(String key, float f) throws JMSException {
        this.properties.put(key, new Float(f));
    }

    public void setDoubleProperty(String key, double d) throws JMSException {
        this.properties.put(key, new Double(d));
    }

    public void setStringProperty(String key, String str) throws JMSException {
        this.properties.put(key, str);
    }

    public void setObjectProperty(String key, Object object) throws JMSException {
        this.properties.put(key, object);
    }

    public void acknowledge() throws JMSException {
        // TODO: acknowledge()?
    }

    public void clearBody() throws JMSException {
        // TODO: clearBody()?
    }


}
