package org.osjava.jms;
public class MemoryTopicSubscriber implements TopicSubscriber extends MessageConsumer{
    public Topic getTopic();
       throws JMSException
    public boolean getNoLocal();
       throws JMSException
}

