package org.osjava.jms;

import javax.jms.ConnectionConsumer;
import javax.jms.QueueConnection;
import javax.jms.Queue;
import javax.jms.QueueSession;
import javax.jms.ServerSessionPool;
import javax.jms.JMSException;

public class MemoryQueueConnection extends MemoryConnection implements QueueConnection {

    public QueueSession createQueueSession(boolean transacted, int acknowledgeMode) throws JMSException {
        return new MemoryQueueSession(transacted, acknowledgeMode);
    }

    public ConnectionConsumer createConnectionConsumer(Queue topic, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new UnsupportedOperationException("Unsupported optional method");
    }

}

