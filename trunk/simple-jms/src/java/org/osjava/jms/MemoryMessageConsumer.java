package org.osjava.jms;
interface MessageConsumer{
    public String getMessageSelector();
       throws JMSException
    public MessageListener getMessageListener();
       throws JMSException
    public void setMessageListener(MessageListener);
       throws JMSException
    public Message receive();
       throws JMSException
    public Message receive(long);
       throws JMSException
    public Message receiveNoWait();
       throws JMSException
    public void close();
       throws JMSException
}

