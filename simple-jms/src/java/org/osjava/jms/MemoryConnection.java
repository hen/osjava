package org.osjava.jms;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;

public class MemoryConnection implements Connection {

    private ExceptionListener listener;
    private String clientId;

    public Session createSession(boolean transacted, int acknowledgeMode) throws JMSException {
        return new MemorySession(transacted, acknowledgeMode);
    }

    public String getClientID() throws JMSException {
        return this.clientId;
    }

    public void setClientID(String clientId) throws JMSException {
        this.clientId = clientId;
    }

    public ConnectionMetaData getMetaData() throws JMSException {
        return new MemoryConnectionMetaData();
    }

    public ExceptionListener getExceptionListener() throws JMSException {
        return listener;
    }

    public void setExceptionListener(ExceptionListener listener) throws JMSException {
        this.listener = listener;
    }

    public void start() throws JMSException {
    }

    public void stop() throws JMSException {
    }

    public void close() throws JMSException {
    }

    public ConnectionConsumer createConnectionConsumer(Destination destination, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        // TODO: Implement?
        throw new UnsupportedOperationException("Not yet implemented. ");
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        // TODO: Implement?
        throw new UnsupportedOperationException("Not yet implemented. ");
    }


}
