package org.osjava.jms;
public class MemoryXAQueueConnection implements XAQueueConnection extends XAConnection,javax.jms.QueueConnection{
    public XAQueueSession createXAQueueSession();
       throws JMSException
    public QueueSession createQueueSession(boolean,int);
       throws JMSException
}

