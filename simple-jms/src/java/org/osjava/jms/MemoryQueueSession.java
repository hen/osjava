package org.osjava.jms;

import javax.jms.JMSException;
import javax.jms.QueueSession;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;
import javax.jms.QueueReceiver;
import javax.jms.QueueBrowser;
import javax.jms.QueueSender;

public class MemoryQueueSession extends MemorySession implements QueueSession {

    public MemoryQueueSession(boolean transacted, int acknowledgeMode) {
        super(transacted, acknowledgeMode);
    }

    public Queue createQueue(String name) throws JMSException {
        return new MemoryQueue(name);
    }

    public QueueReceiver createReceiver(Queue queue) throws JMSException {
        return new MemoryQueueReceiver(queue);
    }

    public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException {
        return new MemoryQueueReceiver(queue, messageSelector);
    }

    public QueueSender createSender(Queue queue) throws JMSException {
        return new MemoryQueueSender(queue);
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return new MemoryQueueBrowser(queue);
    }

    public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        return new MemoryQueueBrowser(queue, messageSelector);
    }

}
