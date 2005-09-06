package org.osjava.jms;
public class MemoryQueueConnection implements QueueConnection extends Connection{
    public QueueSession createQueueSession(boolean,int);
       throws JMSException
    public ConnectionConsumer createConnectionConsumer(javax.jms.Queue,String,javax.jms.ServerSessionPool,int);
       throws JMSException
}

