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

public class MemoryQueue implements Queue {

    private String name;
    private MessageWatcher messageWatcher = null;
    private MessageQueue messageQueue;

    public MemoryQueue(String name) {
        this.name = name;
        this.messageQueue = new MessageQueue();
    }

    public String getQueueName() throws JMSException {
        return this.name;
    }

    void push(Message msg) {
        this.messageQueue.push(msg);
    }

    Message pop() {
        return this.messageQueue.pop();
    }

    Enumeration getEnumeration() {
        return this.messageQueue.getEnumeration();
    }

    void setMessageListener(MessageListener listener) {

        // kill the existing listener
        if (messageWatcher != null){
            messageWatcher.interrupt();
            try {
                messageWatcher.join();
            } catch (InterruptedException e) {
                // we don't care so long as the thread dies.
            }
        }

        messageWatcher = new MessageWatcher( this.messageQueue, listener ) ;
        messageWatcher.start();
    }

    public String toString() {
        return getClass()+"["+this.name+"]";
    }

}

