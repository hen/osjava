package org.osjava.jms;
public class MemoryQueueConnectionFactory implements QueueConnectionFactory extends ConnectionFactory{
    public QueueConnection createQueueConnection();
       throws JMSException
    public QueueConnection createQueueConnection(String,java.lang.String);
       throws JMSException
}

