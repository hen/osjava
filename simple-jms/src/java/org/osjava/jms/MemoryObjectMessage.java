package org.osjava.jms;

import java.io.Serializable;

import javax.jms.ObjectMessage;
import javax.jms.JMSException;

public class MemoryObjectMessage extends MemoryMessage implements ObjectMessage {

    private Serializable ser;

    public void setObject(Serializable ser) throws JMSException {
        this.ser = ser;
    }

    public Serializable getObject() throws JMSException {
        return this.ser;
    }

}

