package org.osjava.jms;
public class MemoryXASession implements XASession extends Session{
    public Session getSession();
       throws JMSException
    public javax.transaction.xa.XAResource getXAResource();
    public boolean getTransacted();
       throws JMSException
    public void commit();
       throws JMSException
    public void rollback();
       throws JMSException
}

