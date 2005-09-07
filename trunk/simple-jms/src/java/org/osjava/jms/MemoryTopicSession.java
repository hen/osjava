package org.osjava.jms;

import javax.jms.JMSException;
import javax.jms.TemporaryTopic;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

public class MemoryTopicSession implements TopicSession {

    public Topic createTopic(String name) throws JMSException {
        return new MemoryTopic(name);
    }

    public TopicSubscriber createSubscriber(Topic topic) throws JMSException {
        return new MemoryTopicSubscriber(topic);
    }

    public TopicSubscriber createSubscriber(Topic topic, String messageSelector, boolean noLocals) throws JMSException {
        return new MemoryTopicSubscriber(topic, messageSelector, noLocals);
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        return new MemoryTopicSubscriber(topic, name);
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocals) throws JMSException {
        return new MemoryTopicSubscriber(topic, name, messageSelector, noLocals);
    }

    public TopicPublisher createPublisher(Topic topic) throws JMSException {
        return new MemoryTopicPublsher(topic);
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return new MemoryTemporaryTopic();
    }

    public void unsubscribe(String name) throws JMSException {
        // TODO: Implement this
    }


}
