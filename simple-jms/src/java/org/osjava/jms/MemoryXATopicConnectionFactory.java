package org.osjava.jms;
public class MemoryXATopicConnectionFactory implements XATopicConnectionFactory extends XAConnectionFactory,javax.jms.TopicConnectionFactory{
    public XATopicConnection createXATopicConnection();
       throws JMSException
    public XATopicConnection createXATopicConnection(String,java.lang.String);
       throws JMSException
}

