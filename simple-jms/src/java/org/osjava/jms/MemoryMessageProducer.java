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


}
