package org.osjava.jms;

import javax.jms.Queue;
import javax.jms.JMSException;

public class MemoryQueue extends MemoryDestination implements Queue {

    private String name;

    public MemoryQueue(String name) {
        this.name = name;
    }

    public String getQueueName() throws JMSException {
        return this.name;
    }

    public String toString() {
        return getClass()+"["+this.name+"]";
    }

}

