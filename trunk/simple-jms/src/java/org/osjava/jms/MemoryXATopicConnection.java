package org.osjava.jms;
public class MemoryXATopicConnection implements XATopicConnection extends XAConnection,javax.jms.TopicConnection{
    public XATopicSession createXATopicSession();
       throws JMSException
    public TopicSession createTopicSession(boolean,int);
       throws JMSException
}

