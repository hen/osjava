package org.osjava.jms;

import javax.jms.TopicConnectionFactory;

public class MemoryTopicConnectionFactory extends MemoryConnectionFactory implements TopicConnectionFactory {

    public TopicConnection createTopicConnection() throws JMSException {
    }

    public TopicConnection createTopicConnection(String user, String passwd) throws JMSException {
    }

}

