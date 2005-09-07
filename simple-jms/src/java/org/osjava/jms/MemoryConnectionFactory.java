package org.osjava.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

public class MemoryConnectionFactory implements ConnectionFactory {

    public Connection createConnection() throws JMSException {
        return new MemoryConnection();
    }

    public Connection createConnection(String user, String passwd) throws JMSException {
        return new MemoryConnection();
    }

}

