package org.osjava.jms;
interface Connection{
    public Session createSession(boolean,int);
       throws JMSException
    public String getClientID();
       throws JMSException
    public void setClientID(String);
       throws JMSException
    public ConnectionMetaData getMetaData();
       throws JMSException
    public ExceptionListener getExceptionListener();
       throws JMSException
    public void setExceptionListener(ExceptionListener);
       throws JMSException
    public void start();
       throws JMSException
    public void stop();
       throws JMSException
    public void close();
       throws JMSException
    public ConnectionConsumer createConnectionConsumer(javax.jms.Destination,String,javax.jms.ServerSessionPool,int);
       throws JMSException
    public ConnectionConsumer createDurableConnectionConsumer(javax.jms.Topic,String,java.lang.String,javax.jms.ServerSessionPool,int);
       throws JMSException
}

