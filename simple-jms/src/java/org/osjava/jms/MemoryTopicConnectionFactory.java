package org.osjava.jms;

import javax.jms.JMSException;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

public class MemoryTopicConnectionFactory extends MemoryConnectionFactory implements TopicConnectionFactory {

    public TopicConnection createTopicConnection() throws JMSException {
        return new MemoryTopicConnection();
    }

    public TopicConnection createTopicConnection(String user, String passwd) throws JMSException {
        return new MemoryTopicConnection();
    }

}

