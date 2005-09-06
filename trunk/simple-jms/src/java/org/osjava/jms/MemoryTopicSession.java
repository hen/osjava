package org.osjava.jms;
public class MemoryTopicSession implements TopicSession extends Session{
    public Topic createTopic(String);
       throws JMSException
    public TopicSubscriber createSubscriber(javax.jms.Topic);
       throws JMSException
    public TopicSubscriber createSubscriber(javax.jms.Topic,String,boolean);
       throws JMSException
    public TopicSubscriber createDurableSubscriber(javax.jms.Topic,String);
       throws JMSException
    public TopicSubscriber createDurableSubscriber(javax.jms.Topic,String,java.lang.String,boolean);
       throws JMSException
    public TopicPublisher createPublisher(javax.jms.Topic);
       throws JMSException
    public TemporaryTopic createTemporaryTopic();
       throws JMSException
    public void unsubscribe(String);
       throws JMSException
}

