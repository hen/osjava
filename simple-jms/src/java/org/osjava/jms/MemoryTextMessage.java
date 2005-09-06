package org.osjava.jms;

import javax.jms.TextMessage;
import javax.jms.JMSException;

public class MemoryTextMessage extends MemoryMessage implements TextMessage {

    public void setText(String text) throws JMSException {
    }

    public String getText() throws JMSException {
    }

}
