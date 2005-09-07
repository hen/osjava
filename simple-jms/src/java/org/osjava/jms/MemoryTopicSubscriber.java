package org.osjava.jms;

import javax.jms.Topic;
import javax.jms.TopicSubscriber;
import javax.jms.JMSException;

public class MemoryTopicSubscriber extends MemoryMessageConsumer implements TopicSubscriber {

    private String name;

    public MemoryTopicSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) {
        super(topic, messageSelector, noLocal);
        this.name = name;
    }

    public MemoryTopicSubscriber(Topic topic, String name) {
        super(topic);
        this.name = name;
    }

    public Topic getTopic() throws JMSException {
        return (Topic) super.getDestination();
    }

    public boolean getNoLocal() throws JMSException {
        return this.noLocal;
    }

}
