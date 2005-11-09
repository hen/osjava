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

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Message;
import javax.jms.Destination;

public class MemoryMessageProducer implements MessageProducer {

    private boolean disableMessageId;
    private boolean disableMessageTimestamp;
    private int deliveryMode;
    private int priority;
    private long timeToLive;
    private Destination destination;

    public MemoryMessageProducer(Destination destination) {
        this.destination = destination;
    }

    public void setDisableMessageID(boolean value) throws JMSException {
        this.disableMessageId = value;
    }

    public boolean getDisableMessageID() throws JMSException {
        return this.disableMessageId;
    }

    public void setDisableMessageTimestamp(boolean value) throws JMSException {
        this.disableMessageTimestamp = value;
    }

    public boolean getDisableMessageTimestamp() throws JMSException {
        return this.disableMessageTimestamp;
    }

    public void setDeliveryMode(int deliveryMode) throws JMSException {
        this.deliveryMode = deliveryMode;
    }

    public int getDeliveryMode() throws JMSException {
        return this.deliveryMode;
    }

    public void setPriority(int priority) throws JMSException {
        this.priority = priority;
    }

    public int getPriority() throws JMSException {
        return this.priority;
    }

    public void setTimeToLive(long ttl) throws JMSException {
        this.timeToLive = ttl;
    }

    public long getTimeToLive() throws JMSException {
        return this.timeToLive;
    }

    public Destination getDestination() throws JMSException {
        return this.destination;
    }

    public void close() throws JMSException {
        // TODO: Implement this
    }

    public void send(Message msg) throws JMSException {
        // TODO: Implement this
    }

    public void send(Message msg, int deliveryMode, int priority, long timeToLive) throws JMSException {
        // TODO: Implement this
    }

    public void send(Destination destination, Message msg) throws JMSException {
        // TODO: Implement this
    }

    public void send(Destination destination, Message msg, int deliveryMode, int priority, long timeToLive) throws JMSException {
        // TODO: Implement this
    }

    protected void setJMSHeaders(Destination destination, Message msg, int deliveryMode, int priority, long timeToLive) throws JMSException {
        msg.setJMSDestination(destination);
        msg.setJMSDeliveryMode(deliveryMode);
        if(!getDisableMessageID()) {
            msg.setJMSMessageID("ID:"+System.currentTimeMillis());  // for want of a better ID system
        }
        if(!getDisableMessageTimestamp()) {
            msg.setJMSTimestamp(System.currentTimeMillis());
        }
        msg.setJMSPriority(priority);
        msg.setJMSExpiration(timeToLive);
    }

}
