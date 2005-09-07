package org.osjava.jms;

import javax.jms.ConnectionConsumer;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;

// TODO: There are many optional parts of the API that 
// do things with these, we're not implementing any yet
public class MemoryConnectionConsumer implements ConnectionConsumer {

    public ServerSessionPool getServerSessionPool() throws JMSException {
        // TODO: Implement this
        return null;
    }

    public void close() throws JMSException {
        // TODO: Implement this
    }

}
