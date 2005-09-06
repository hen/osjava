package org.osjava.jms;
public class MemoryTopicConnectionFactory implements TopicConnectionFactory extends ConnectionFactory{
    public TopicConnection createTopicConnection();
       throws JMSException
    public TopicConnection createTopicConnection(String,java.lang.String);
       throws JMSException
}

