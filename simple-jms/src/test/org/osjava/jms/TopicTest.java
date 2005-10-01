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
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSubscriber;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import junit.framework.TestCase;
import org.osjava.jms.MemoryTopicConnectionFactory;

public class TopicTest extends TestCase implements MessageListener {

    private Thread lastThread;
    private Message lastMessage;

    private TopicSession tss;
    private Topic t;
    private TopicPublisher tp;
    private TopicSubscriber ts;

    public TopicTest(String name) {
        super(name);
   }

    public void setUp() throws Exception {
        TopicConnectionFactory tcf = new MemoryTopicConnectionFactory();
        TopicConnection tc = tcf.createTopicConnection();
        tss = tc.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        // NOTE: This is not the correct way to make a Topic, 
        // We should be getting it from JNDI or something
        // probably by depending on simple-jndi and implementing converters
        t = new MemoryTopic("Test Topic");
        tp = tss.createPublisher(t);
        ts = tss.createSubscriber(t);
    }

    public void tearDown() {
    }

    public void onMessage(Message message) {
        this.lastThread = Thread.currentThread();
        this.lastMessage = message;
    }

    public void testAsyncMessageDelivery() throws JMSException {
        ts.setMessageListener(this);
        Message m = tss.createMessage();
        TestTimer timer = new TestTimer("Message not received within alloted time", 1000L);
        timer.startClock();
        tp.send(m);
        while(this.lastMessage == null) {
            Thread.yield();
            timer.failIfOverdue();
        }
        assertEquals("Asynchronous message damaged", m, this.lastMessage);
        assertTrue("Same thread was used", Thread.currentThread() != this.lastThread);
    }

}
