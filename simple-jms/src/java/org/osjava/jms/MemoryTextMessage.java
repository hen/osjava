package org.osjava.jms;
public class MemoryTextMessage implements TextMessage extends Message{
    public void setText(String);
       throws JMSException
    public String getText();
       throws JMSException
}

