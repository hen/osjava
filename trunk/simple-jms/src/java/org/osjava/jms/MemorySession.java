package org.osjava.jms;
public class MemorySession implements Session extends Runnable{
    public static final int AUTO_ACKNOWLEDGE;
    public static final int CLIENT_ACKNOWLEDGE;
    public static final int DUPS_OK_ACKNOWLEDGE;
    public static final int SESSION_TRANSACTED;
    public BytesMessage createBytesMessage();
       throws JMSException
    public MapMessage createMapMessage();
       throws JMSException
    public Message createMessage();
       throws JMSException
    public ObjectMessage createObjectMessage();
       throws JMSException
    public ObjectMessage createObjectMessage(java.io.Serializable);
       throws JMSException
    public StreamMessage createStreamMessage();
       throws JMSException
    public TextMessage createTextMessage();
       throws JMSException
    public TextMessage createTextMessage(String);
       throws JMSException
    public boolean getTransacted();
       throws JMSException
    public int getAcknowledgeMode();
       throws JMSException
    public void commit();
       throws JMSException
    public void rollback();
       throws JMSException
    public void close();
       throws JMSException
    public void recover();
       throws JMSException
    public MessageListener getMessageListener();
       throws JMSException
    public void setMessageListener(MessageListener);
       throws JMSException
    public void run();
    public MessageProducer createProducer(javax.jms.Destination);
       throws JMSException
    public MessageConsumer createConsumer(javax.jms.Destination);
       throws JMSException
    public MessageConsumer createConsumer(javax.jms.Destination,String);
       throws JMSException
    public MessageConsumer createConsumer(javax.jms.Destination,String,boolean);
       throws JMSException
    public Queue createQueue(String);
       throws JMSException
    public Topic createTopic(String);
       throws JMSException
    public TopicSubscriber createDurableSubscriber(javax.jms.Topic,String);
       throws JMSException
    public TopicSubscriber createDurableSubscriber(javax.jms.Topic,String,java.lang.String,boolean);
       throws JMSException
    public QueueBrowser createBrowser(javax.jms.Queue);
       throws JMSException
    public QueueBrowser createBrowser(javax.jms.Queue,String);
       throws JMSException
    public TemporaryQueue createTemporaryQueue();
       throws JMSException
    public TemporaryTopic createTemporaryTopic();
       throws JMSException
    public void unsubscribe(String);
       throws JMSException
}

