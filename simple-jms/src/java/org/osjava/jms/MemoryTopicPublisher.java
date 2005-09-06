package org.osjava.jms;
public class MemoryTopicPublisher implements TopicPublisher extends MessageProducer{
    public Topic getTopic();
       throws JMSException
    public void publish(Message);
       throws JMSException
    public void publish(Message,int,int,long);
       throws JMSException
    public void publish(Topic,javax.jms.Message);
       throws JMSException
    public void publish(Topic,javax.jms.Message,int,int,long);
       throws JMSException
}

