package org.osjava.jms;

import javax.jms.TopicPublisher;
import javax.jms.Topic;
import javax.jms.JMSException;
import javax.jms.Message;

public class MemoryTopicPublisher extends MemoryMessageProducer implements TopicPublisher {

    private Topic topic;

    public MemoryTopicPublisher(Topic topic) {
        super(topic);
        this.topic = topic;
    }

    public Topic getTopic() throws JMSException {
        return this.topic;
    }

    public void publish(Message msg) throws JMSException {
        // TODO: Implement this
    }

    public void publish(Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        // TODO: Implement this
    }

    public void publish(Topic topic, Message msg) throws JMSException {
        // TODO: Implement this
    }

    public void publish(Topic topic, Message message, int deliveryMode, int priority, long timeToLive) throws JMSException {
        // TODO: Implement this
    }


}

