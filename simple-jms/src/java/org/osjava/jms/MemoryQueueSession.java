package org.osjava.jms;
public class MemoryQueueSession implements QueueSession extends Session{
    public Queue createQueue(String);
       throws JMSException
    public QueueReceiver createReceiver(javax.jms.Queue);
       throws JMSException
    public QueueReceiver createReceiver(javax.jms.Queue,String);
       throws JMSException
    public QueueSender createSender(javax.jms.Queue);
       throws JMSException
    public QueueBrowser createBrowser(javax.jms.Queue);
       throws JMSException
    public QueueBrowser createBrowser(javax.jms.Queue,String);
       throws JMSException
    public TemporaryQueue createTemporaryQueue();
       throws JMSException
}

