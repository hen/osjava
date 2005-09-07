package org.osjava.jms;

import java.util.Enumeration;

import javax.jms.ConnectionMetaData;

public class MemoryConnectionMetaData implements ConnectionMetaData {

    public String getJMSVersion() {
        return getJMSMajorVersion()+"."+getJMSMinorVersion();
    }

    // J2EE 1.4 anyway
    public int getJMSMajorVersion() {
        return 1;
    }

    public int getJMSMinorVersion() {
        return 4;
    }

    public String getJMSProviderName() {
        return "Simple-JMS";
    }

    public String getProviderVersion() {
        return getProviderMajorVersion()+"."+getProviderMinorVersion();
    }

    // TODO: Use maven trickery to search and replace this
    public int getProviderMajorVersion() {
        return 0;
    }

    // TODO: Use maven trickery to search and replace this
    public int getProviderMinorVersion() {
        return 1;
    }

    public Enumeration getJMSXPropertyNames() {
        return null;
    }


}

