package org.osjava.jms;

import javax.jms.TextMessage;
import javax.jms.JMSException;

public class MemoryTextMessage extends MemoryMessage implements TextMessage {

    private String text;

    public void setText(String text) throws JMSException {
        this.text = text;
    }

    public String getText() throws JMSException {
        return this.text;
    }

}
