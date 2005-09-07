package org.osjava.jms;

import javax.jms.TemporaryTopic;
import javax.jms.JMSException;

public class MemoryTemporaryTopic extends MemoryTopic implements TemporaryTopic {
    public MemoryTemporaryTopic(String name) {
        super(name);
    }

    public void delete() throws JMSException {
        // TODO: Tell someone I'm dead
    }
}

