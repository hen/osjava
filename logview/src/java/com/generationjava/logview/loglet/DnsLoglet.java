package com.generationjava.logview.loglet;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.OverlayLogEvent;
import com.generationjava.logview.log.SimpleLogField;

public class DnsLoglet extends SinkLoglet {

    private String field;

    public DnsLoglet(String field) {
        this("Nslookup-"+field, field);
    }
    public DnsLoglet(String name, String field) {
        super(name);
        this.field = field;
    }

    public LogEvent parseEvent(LogIterator logIt) throws LogViewException {
        LogEvent event = logIt.nextLogEvent();
        String logfield = event.get(this.field).getValue().toString();
        try {
            InetAddress inet = InetAddress.getByName(logfield);
            if(inet.getHostName().equals(inet.getHostAddress())) {
                return event;
            }
            OverlayLogEvent overlay = new OverlayLogEvent(event);
            LogField logField = new SimpleLogField(this.field, inet.getHostName());
            overlay.overlay(this.field, logField);
            return overlay;
        } catch(UnknownHostException uhe) {
            System.err.println("Non-existant IP: "+logfield);
            return event;
        }
    }

}
