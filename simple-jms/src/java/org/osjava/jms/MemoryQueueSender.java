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

import javax.jms.Message;
import javax.jms.QueueSender;
import javax.jms.Queue;
import javax.jms.JMSException;

public class MemoryQueueSender extends MemoryMessageProducer implements QueueSender {

    private MemoryQueue queue;

    public MemoryQueueSender(MemoryQueue queue) {
        super(queue);
        this.queue = queue;
    }

    public Queue getQueue() throws JMSException {
        return this.queue;
    }

    public void send(Message msg) throws JMSException {
        send(this.queue, msg);
    }

    // TODO: Implement options?
    public void send(Message msg, int deliveryMode, int priority, long timeToLive) throws JMSException {
        send(this.queue, msg, deliveryMode, priority, timeToLive);
    }

    public void send(Queue queue, Message msg) throws JMSException {
        send(queue, msg, -1, 0, 0);    // TODO: What's the default delivery mode?
    }

    // TODO: Implement options?
    public void send(Queue queue, Message msg, int deliveryMode, int priority, long timeToLive) throws JMSException {
        super.setJMSHeaders(queue, msg, deliveryMode, priority, timeToLive);
        ((MemoryQueue)queue).push(msg);
    }

}

