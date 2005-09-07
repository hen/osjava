package org.osjava.jms;

import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.JMSException;

public class MemoryQueueConnectionFactory extends MemoryConnectionFactory implements QueueConnectionFactory {

    public QueueConnection createQueueConnection() throws JMSException {
        return new MemoryQueueConnection();
    }

    public QueueConnection createQueueConnection(String user, String passwd) throws JMSException {
        return new MemoryQueueConnection();
    }

}

