package org.osjava.jms;

import javax.jms.TemporaryQueue;
import javax.jms.JMSException;

public class MemoryTemporaryQueue extends MemoryQueue implements TemporaryQueue {
    public MemoryTemporaryQueue(String name) {
        super(name);
    }

    public void delete() throws JMSException {
        // TODO: Tell someone I'm dead
    }
}

