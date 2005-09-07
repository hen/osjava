package org.osjava.jms;

import javax.jms.ServerSessionPool;
import javax.jms.ServerSession;
import javax.jms.JMSException;

public class MemoryServerSessionPool implements ServerSessionPool {

    public ServerSession getServerSession() throws JMSException {
        throw new UnsupportedOperationException("Optional class not implemented. ");
    }

}
