package org.osjava.jms;

import javax.jms.Topic;
import javax.jms.JMSException;

public class MemoryTopic extends MemoryDestination implements Topic {

    private String name;

    public MemoryTopic(String name) {
        this.name = name;
    }

    public String getTopicName() throws JMSException {
        return this.name;
    }

    public String toString() {
        return getClass()+"["+this.name+"]";
    }

}

