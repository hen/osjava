package org.osjava.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Destination;

public class MemoryMessageConsumer implements MessageConsumer {

    private MessageListener listener;
    private Destination destination;
    private String messageSelector;
    protected boolean noLocal;

    public MemoryMessageConsumer(Destination destination) {
        this.destination = destination;
    }

    public MemoryMessageConsumer(Destination destination, String messageSelector) {
        this.destination = destination;
        this.messageSelector = messageSelector;
    }

    public MemoryMessageConsumer(Destination destination, String messageSelector, boolean noLocal) {
        this.destination = destination;
        this.messageSelector = messageSelector;
        this.noLocal = noLocal;
    }

    protected Destination getDestination() throws JMSException {
        return this.destination;
    }

    public String getMessageSelector() throws JMSException {
        return this.messageSelector;
    }

    public MessageListener getMessageListener() throws JMSException {
        return this.listener;
    }

    public void setMessageListener(MessageListener listener) throws JMSException {
        this.listener = listener;
    }

    public Message receive() throws JMSException {
        // TODO: Implement this
        return null;
    }

    public Message receive(long timeout) throws JMSException {
        // TODO: Implement this
        return null;
    }

    public Message receiveNoWait() throws JMSException {
        // TODO: Implement this
        return null;
    }

    public void close() throws JMSException {
        // TODO: Implement this
    }

}
