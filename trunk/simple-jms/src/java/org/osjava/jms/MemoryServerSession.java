package org.osjava.jms;

import javax.jms.JMSException;
import javax.jms.ServerSession;
import javax.jms.Session;

public class MemoryServerSession implements ServerSession {

    private Session session;

    public Session getSession() throws JMSException {
        // TODO: How does this get instantiated? Is it a MemorySession?
        return this.session;
    }

    public void start() throws JMSException {
        // TODO: Threading needed right?
        this.session.run();
    }

}
