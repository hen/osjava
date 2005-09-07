package org.osjava.jms;

import javax.jms.ConnectionConsumer;
import javax.jms.TopicConnection;
import javax.jms.Topic;
import javax.jms.ServerSessionPool;
import javax.jms.JMSException;

public class MemoryTopicConnection extends MemoryConnection implements TopicConnection {

    public TopicSession createTopicSession(boolean transacted, int acknowledgeMode) throws JMSException {
        return new MemoryTopicSession(transacted, acknowledgeMode);
    }

    public ConnectionConsumer createConnectionConsumer(Topic topic, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new UnsupportedOperationException("Unsupported optional method");
    }

    public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
        throw new UnsupportedOperationException("Unsupported optional method");
    }

}

