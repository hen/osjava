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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import junit.framework.TestCase;
import org.osjava.jms.MemoryQueueConnectionFactory;

public class QueueTest extends TestCase implements MessageListener {

    private Message lastMessage;
    private QueueConnectionFactory qcf = null;
    private QueueConnection qc = null;
    private QueueSession qss = null;

    private Queue q = null;
    private QueueSender qs = null;
    private QueueReceiver qr = null;

    public QueueTest(String name) {
        super(name);
   }

    public void setUp() throws Exception {
        qcf = new MemoryQueueConnectionFactory();
        qc = qcf.createQueueConnection();
        qss = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        // NOTE: This is not the correct way to make a Queue, 
        // We should be getting it from JNDI or something
        // probably by depending on simple-jndi and implementing converters
        q = qss.createQueue("Test Q");
        qs = qss.createSender(q);
        qr = qss.createReceiver(q);
    }

    public void tearDown() {
    }

    public void testAsyncMessageDelivery() throws Exception {
        qr.setMessageListener(this);
        Message m = qss.createMessage();
        qs.send(m);
        assertEquals(this.lastMessage, m);
    }

    public void testSyncMessageDelivery() throws Exception {
        Message sent = qss.createMessage();
        qs.send(sent);
        Message received = qr.receive();
        
        assertEquals(received, sent);
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
        
        assertFalse (thread.isAlive()); //thing should have finished ages ago!
        thread.destroy();
    }

    public void onMessage(Message m) {
        this.lastMessage = m;
    }

}
