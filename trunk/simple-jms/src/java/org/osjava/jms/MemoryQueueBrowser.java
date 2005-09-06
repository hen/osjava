package org.osjava.jms;
interface QueueBrowser{
    public Queue getQueue();
       throws JMSException
    public String getMessageSelector();
       throws JMSException
    public java.util.Enumeration getEnumeration();
       throws JMSException
    public void close();
       throws JMSException
}

