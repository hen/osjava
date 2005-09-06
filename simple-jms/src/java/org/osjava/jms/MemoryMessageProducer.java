package org.osjava.jms;
interface MessageProducer{
    public void setDisableMessageID(boolean);
       throws JMSException
    public boolean getDisableMessageID();
       throws JMSException
    public void setDisableMessageTimestamp(boolean);
       throws JMSException
    public boolean getDisableMessageTimestamp();
       throws JMSException
    public void setDeliveryMode(int);
       throws JMSException
    public int getDeliveryMode();
       throws JMSException
    public void setPriority(int);
       throws JMSException
    public int getPriority();
       throws JMSException
    public void setTimeToLive(long);
       throws JMSException
    public long getTimeToLive();
       throws JMSException
    public Destination getDestination();
       throws JMSException
    public void close();
       throws JMSException
    public void send(Message);
       throws JMSException
    public void send(Message,int,int,long);
       throws JMSException
    public void send(Destination,javax.jms.Message);
       throws JMSException
    public void send(Destination,javax.jms.Message,int,int,long);
       throws JMSException
}

