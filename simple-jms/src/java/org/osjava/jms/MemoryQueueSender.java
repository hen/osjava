package org.osjava.jms;

import javax.jms.Message;
import javax.jms.QueueSender;
import javax.jms.Queue;
import javax.jms.JMSException;

public class MemoryQueueSender extends MemoryMessageProducer implements QueueSender {

    private Queue queue;

    public MemoryQueueSender(Queue queue) {
        super(queue);
        this.queue = queue;
    }

    public Queue getQueue() throws JMSException {
        return this.queue;
    }

    public void send(Message msg) throws JMSException {
        // TODO
    }

    public void send(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        // TODO
    }

    public void send(Queue queue, Message msg) throws JMSException {
        // TODO
    }

    public void send(Queue queue, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        // TODO
    }

}

