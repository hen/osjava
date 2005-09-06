package org.osjava.jms;
public class MemoryXAConnection implements XAConnection extends Connection{
    public XASession createXASession();
       throws JMSException
    public Session createSession(boolean,int);
       throws JMSException
}

