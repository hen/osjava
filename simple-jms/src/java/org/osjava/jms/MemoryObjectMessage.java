package org.osjava.jms;
public class MemoryObjectMessage implements ObjectMessage extends Message{
    public void setObject(java.io.Serializable);
       throws JMSException
    public java.io.Serializable getObject();
       throws JMSException
}

