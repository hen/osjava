package org.osjava.jms;

import javax.jms.QueueReceiver;
import javax.jms.Queue;
import javax.jms.JMSException;

public class MemoryQueueReceiver extends MemoryMessageConsumer implements QueueReceiver {

    private Queue queue;

    public MemoryQueueReceiver(Queue queue) {
        super(queue);
        this.queue = queue;
    }

    public Queue getQueue() throws JMSException {
        return queue;
    }

}

