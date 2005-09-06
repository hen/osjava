package org.osjava.jms;
interface ServerSession{
    public Session getSession();
       throws JMSException
    public void start();
       throws JMSException
}

