package org.osjava.jms;
interface ServerSessionPool{
    public ServerSession getServerSession();
       throws JMSException
}

