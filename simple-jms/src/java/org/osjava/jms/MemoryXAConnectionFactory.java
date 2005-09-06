package org.osjava.jms;
interface XAConnectionFactory{
    public XAConnection createXAConnection();
       throws JMSException
    public XAConnection createXAConnection(String,java.lang.String);
       throws JMSException
}

