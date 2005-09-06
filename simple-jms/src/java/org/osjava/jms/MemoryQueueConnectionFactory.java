package org.osjava.jms;

import javax.jms.QueueConnectionFactory;
import javax.jms.JMSException;

public class MemoryQueueConnectionFactory extends MemoryConnectionFactory implements QueueConnectionFactory {

    public QueueConnection createQueueConnection() throws JMSException {
    }

    public QueueConnection createQueueConnection(String user, String passwd) throws JMSException {
    }

}

