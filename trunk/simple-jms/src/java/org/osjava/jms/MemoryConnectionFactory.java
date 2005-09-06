package org.osjava.jms;
interface ConnectionFactory{
    public Connection createConnection();
       throws JMSException
    public Connection createConnection(String,java.lang.String);
       throws JMSException
}

