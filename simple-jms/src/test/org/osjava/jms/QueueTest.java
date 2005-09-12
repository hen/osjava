package org.osjava.jms;

import junit.framework.TestCase;

import javax.jms.*;

public class QueueTest extends TestCase implements MessageListener {

    private Message lastMessage;

    public QueueTest(String name) {
        super(name);
    }

    public void setUp() {
    }

    public void tearDown() {
    }

    public void testQueue() throws Exception {
        QueueConnectionFactory qcf = new MemoryQueueConnectionFactory();
        QueueConnection qc = qcf.createQueueConnection();
        QueueSession qss = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        // NOTE: This is not the correct way to make a Queue, 
        // We should be getting it from JNDI or something
        // probably by depending on simple-jndi and implementing converters
        Queue q = qss.createQueue("Test Q");
        QueueSender qs = qss.createSender(q);
        QueueReceiver qr = qss.createReceiver(q);
        qr.setMessageListener(this);
        Message m = qss.createMessage();
        qs.send(m);
        assertEquals(this.lastMessage, m);
    }

    public void onMessage(Message m) {
        this.lastMessage = m;
    }

}
