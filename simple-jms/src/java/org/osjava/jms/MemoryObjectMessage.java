package org.osjava.jms;

import java.io.Serializable;

import javax.jms.ObjectMessage;

public class MemoryObjectMessage extends MemoryMessage implements ObjectMessage {

    public void setObject(Serializable ser) throws JMSException {
    }

    public Serializable getObject() throws JMSException {
    }

}

