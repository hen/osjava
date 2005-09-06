package org.osjava.jms;
interface ConnectionConsumer{
    public ServerSessionPool getServerSessionPool();
       throws JMSException
    public void close();
       throws JMSException
}

