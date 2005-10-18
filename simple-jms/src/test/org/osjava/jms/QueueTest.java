/*
 * Copyright (c) 2005, Steve Heath, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of OSJava nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.jms;

import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import javax.naming.InitialContext;

import junit.framework.TestCase;

public class QueueTest extends TestCase implements MessageListener {

    private Message lastMessage;
    private Thread lastThread;

    private QueueSession qss;
    private QueueSender qs;
    private Queue q;
    private QueueReceiver qr;

    public QueueTest(String name) {
        super(name);
   }

    public void setUp() throws Exception {
        InitialContext ctxt = new InitialContext();
        QueueConnectionFactory qcf = (QueueConnectionFactory) ctxt.lookup("org.osjava.jms.QueueConnectionFactory");
        QueueConnection qc = qcf.createQueueConnection();
        qss = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        q = (Queue) ctxt.lookup("Test Q");
        qs = qss.createSender(q);
        qr = qss.createReceiver(q);
    }

    public void tearDown() {
    }

    public void testAsyncMessageDelivery() throws Exception {
        qr.setMessageListener(this);
        Message m = qss.createMessage();
        TestTimer timer = new TestTimer("Message not received within alloted time", 1000L);
        timer.startClock();
        qs.send(m);
        while(this.lastMessage == null) {
            Thread.yield();
            timer.failIfOverdue();
        }
        assertEquals("Asynchronous message damaged", m, this.lastMessage);
        assertTrue("Same thread was used", Thread.currentThread() != this.lastThread);
    }

    public void onMessage(Message m) {
        this.lastThread = Thread.currentThread();
        this.lastMessage = m;
    }

    public void testSyncMessageDelivery() throws Exception {
        Message sent = qss.createMessage();
        qs.send(sent);
        Message received = qr.receive();
        
        assertEquals("Synchronous message damaged", received, sent);
    }

    /**
     * Test the timeout works. Note this is a rough test because it's too hard to time it exactly.
     * @throws Exception
     */
    public void testTimedSyncMessageDelivery() throws Exception {
        final int timeout = 100;
            
        Thread thread = new Thread () {
            public void run () {
                try {
                    qr.receive(timeout);
                } catch (JMSException e) {
                    throw new RuntimeException("Exception while trying to receive", e);
                }
            }
        };
        thread.start();
        thread.join(timeout); // a bit spurious, I know..
        // the problem is that I'm waitingthe same timeout as the receive method,
        // however, this thread has done other things in the mean time so there's no
        // accuracy here..
        
        assertFalse ("Thread should have timed out", thread.isAlive());
        thread.interrupt();
    }
    
    public void testBrowser () throws Exception {
        qs.send (qss.createTextMessage("test1"));
        qs.send (qss.createTextMessage("test2"));
        qs.send (qss.createTextMessage("test3"));
        
        QueueBrowser qb = qss.createBrowser(q);
        
        Enumeration e = qb.getEnumeration();
        TextMessage m = null;
        assertTrue("has at least one element", e.hasMoreElements());
        m = (TextMessage)e.nextElement();
        assertEquals("1st message is correct", "test1", m.getText());
        assertTrue("has at least two elements", e.hasMoreElements());
        m = (TextMessage)e.nextElement();
        assertEquals("2nd message is correct", "test2", m.getText());
        assertTrue("has at least three elements", e.hasMoreElements());
        m = (TextMessage)e.nextElement();
        assertEquals("3rd message is correct", "test3", m.getText());
        assertFalse ("No more elements", e.hasMoreElements());
    }

}
