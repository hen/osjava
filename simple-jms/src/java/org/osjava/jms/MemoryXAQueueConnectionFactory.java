package org.osjava.jms;
public class MemoryXAQueueConnectionFactory implements XAQueueConnectionFactory extends XAConnectionFactory,javax.jms.QueueConnectionFactory{
    public XAQueueConnection createXAQueueConnection();
       throws JMSException
    public XAQueueConnection createXAQueueConnection(String,java.lang.String);
       throws JMSException
}

