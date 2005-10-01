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

import java.io.Serializable;
import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TemporaryQueue;
import javax.jms.TemporaryTopic;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

public class MemorySession implements Session {

    private boolean transacted;
    private int acknowledgeMode;
    private MessageListener listener;

    public MemorySession(boolean transacted, int acknowledgeMode) {
        this.transacted = transacted;
        this.acknowledgeMode = acknowledgeMode;
    }

    public BytesMessage createBytesMessage() throws JMSException {
        return new MemoryBytesMessage();
    }

    public MapMessage createMapMessage() throws JMSException {
        return new MemoryMapMessage();
    }

    public Message createMessage() throws JMSException {
        return new MemoryMessage();
    }

    public ObjectMessage createObjectMessage() throws JMSException {
        return new MemoryObjectMessage();
    }

    public ObjectMessage createObjectMessage(Serializable ser) throws JMSException {
        ObjectMessage msg = new MemoryObjectMessage();
        msg.setObject(ser);
        return msg;
    }

    public StreamMessage createStreamMessage() throws JMSException {
        return new MemoryStreamMessage();
    }

    public TextMessage createTextMessage() throws JMSException {
        return new MemoryTextMessage();
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        TextMessage msg = new MemoryTextMessage();
        msg.setText(text);
        return msg;
    }

    public boolean getTransacted() throws JMSException {
        return this.transacted;
    }

    public int getAcknowledgeMode() throws JMSException {
        return this.acknowledgeMode;
    }

    public void commit() throws JMSException {
        // TODO: Implement this
    }

    public void rollback() throws JMSException {
        // TODO: Implement this
    }

    public void close() throws JMSException {
        // TODO: Implement this
    }

    public void recover() throws JMSException {
        // TODO: Implement this
    }

    public MessageListener getMessageListener() throws JMSException {
        return this.listener;
    }

    public void setMessageListener(MessageListener listener) throws JMSException {
        this.listener = listener;
    }

    public void run() {
        // TODO: Implement this
    }

    public MessageProducer createProducer(Destination destination) throws JMSException {
        return new MemoryMessageProducer(destination);
    }

    public MessageConsumer createConsumer(Destination destination) throws JMSException {
        return new MemoryMessageConsumer(destination);
    }

    public MessageConsumer createConsumer(Destination destination, String messageSelector) throws JMSException {
        return new MemoryMessageConsumer(destination, messageSelector);
    }

    public MessageConsumer createConsumer(Destination destination, String messageSelector, boolean noLocal) throws JMSException {
        return new MemoryMessageConsumer(destination, messageSelector, noLocal);
    }

    public Queue createQueue(String name) throws JMSException {
        return new MemoryQueue(name);
    }

    public Topic createTopic(String name) throws JMSException {
        return new MemoryTopic(name);
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name) throws JMSException {
        return new MemoryTopicSubscriber( (MemoryTopic) topic, name);
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String name, String messageSelector, boolean noLocal) throws JMSException {
        return new MemoryTopicSubscriber( (MemoryTopic) topic, name, messageSelector, noLocal);
    }

    public QueueBrowser createBrowser(Queue queue) throws JMSException {
        return new MemoryQueueBrowser((MemoryQueue)queue);
    }

    public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException {
        return new MemoryQueueBrowser((MemoryQueue)queue, messageSelector);
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return new MemoryTemporaryQueue("TODO: Temporary name?");
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return new MemoryTemporaryTopic("TODO: Temporary name?");
    }

    public void unsubscribe(String name) throws JMSException {
        // TODO: Implement this
    }

}

