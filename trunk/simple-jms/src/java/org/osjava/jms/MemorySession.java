package org.osjava.jms;

import java.io.Serializable;
import javax.jms.*;

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
        return new MemoryObjectMessage(ser);
    }

    public StreamMessage createStreamMessage() throws JMSException {
        return new MemoryStreamMessage();
    }

    public TextMessage createTextMessage() throws JMSException {
        return new MemoryTextMessage();
    }

    public TextMessage createTextMessage(String text) throws JMSException {
        return new MemoryTextMessage(text);
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

    public void run() throws JMSException {
        // TODO: Implement this
    }

    public MessageProducer createProducer(Destination destination) throws JMSException {
    }

    public MessageConsumer createConsumer(Destination destination) throws JMSException {
    }

    public MessageConsumer createConsumer(Destination destination, String) throws JMSException {
    }

    public MessageConsumer createConsumer(Destination destination, String, boolean) throws JMSException {
    }

    public Queue createQueue(String name) throws JMSException {
        return new MemoryQueue(name);
    }

    public Topic createTopic(String name) throws JMSException {
        return new MemoryTopic(name);
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String) throws JMSException {
    }

    public TopicSubscriber createDurableSubscriber(Topic topic, String, String, boolean) throws JMSException {
    }

    public QueueBrowser createBrowser(Queue) throws JMSException {
    }

    public QueueBrowser createBrowser(Queue, String) throws JMSException {
    }

    public TemporaryQueue createTemporaryQueue() throws JMSException {
        return new MemoryTemporaryQueue();
    }

    public TemporaryTopic createTemporaryTopic() throws JMSException {
        return new MemoryTemporaryTopic();
    }

    public void unsubscribe(String) throws JMSException {
        // TODO: Implement this
    }

}

