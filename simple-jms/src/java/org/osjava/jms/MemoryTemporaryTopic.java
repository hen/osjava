package org.osjava.jms;

import javax.jms.TemporaryTopic;
import javax.jms.JMSException;

public class MemoryTemporaryTopic extends MemoryTopic implements TemporaryTopic {
    public void delete() throws JMSException {
        // TODO: Tell someone I'm dead
    }
}

