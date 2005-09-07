package org.osjava.jms;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;

public class MemoryQueueBrowser implements QueueBrowser {

    private Queue queue;
    private String messageSelector;

    public MemoryQueueBrowser(Queue queue) {
        this.queue = queue;
    }

    public MemoryQueueBrowser(Queue queue, String messageSelector) {
        this.queue = queue;
        this.messageSelector = messageSelector;
    }

    public Queue getQueue() throws JMSException {
        return this.queue;
    }

    public String getMessageSelector() throws JMSException {
        return this.messageSelector;
    }

    public Enumeration getEnumeration() throws JMSException {
        // TODO: Implement this
        return null;
    }

    public void close() throws JMSException {
        // TODO: Implement this
    }

}
