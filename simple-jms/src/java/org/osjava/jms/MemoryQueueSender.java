package org.osjava.jms;
public class MemoryQueueSender implements QueueSender extends MessageProducer{
    public Queue getQueue();
       throws JMSException
    public void send(Message);
       throws JMSException
    public void send(Message,int,int,long);
       throws JMSException
    public void send(Queue,javax.jms.Message);
       throws JMSException
    public void send(Queue,javax.jms.Message,int,int,long);
       throws JMSException
}

