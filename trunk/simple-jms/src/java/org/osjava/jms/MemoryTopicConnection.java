package org.osjava.jms;
public class MemoryTopicConnection implements TopicConnection extends Connection{
    public TopicSession createTopicSession(boolean,int);
       throws JMSException
    public ConnectionConsumer createConnectionConsumer(javax.jms.Topic,String,javax.jms.ServerSessionPool,int);
       throws JMSException
    public ConnectionConsumer createDurableConnectionConsumer(javax.jms.Topic,String,java.lang.String,javax.jms.ServerSessionPool,int);
       throws JMSException
}

