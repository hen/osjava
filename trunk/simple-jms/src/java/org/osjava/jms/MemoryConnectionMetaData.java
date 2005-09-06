package org.osjava.jms;
interface ConnectionMetaData{
    public String getJMSVersion();
       throws JMSException
    public int getJMSMajorVersion();
       throws JMSException
    public int getJMSMinorVersion();
       throws JMSException
    public String getJMSProviderName();
       throws JMSException
    public String getProviderVersion();
       throws JMSException
    public int getProviderMajorVersion();
       throws JMSException
    public int getProviderMinorVersion();
       throws JMSException
    public java.util.Enumeration getJMSXPropertyNames();
       throws JMSException
}

