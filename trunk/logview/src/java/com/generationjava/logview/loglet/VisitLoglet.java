package com.generationjava.logview.loglet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.collections.MultiMap;

import org.apache.commons.lang.StringUtils;

import com.generationjava.logview.Log;
import com.generationjava.logview.Loglet;
import com.generationjava.logview.LogEvent;
import com.generationjava.logview.LogField;
import com.generationjava.logview.LogFilter;
import com.generationjava.logview.LogType;
import com.generationjava.logview.LogTypes;
import com.generationjava.logview.LogIterator;
import com.generationjava.logview.LogViewException;

import com.generationjava.logview.log.SimpleLogEvent;
import com.generationjava.logview.log.SimpleLogField;

/// A very specialised Loglet. a MergedFieldLoglet would be nice, 
/// but it's hard to genericise to that point in a sensible way.
/// TODO: fix bug with 1 large visit overlapping 2 small visits
public class VisitLoglet extends AbstractLoglet {

    private String ipField;
    private String dateField;
    private String[] fieldNames;

    private HashMap currentIpMap = new HashMap();
    private SequencedHashMap queueMap = new SequencedHashMap();

    // is the log iterator dead?

    public VisitLoglet(String ipField, String dateField) {
        this("Visit", ipField, dateField);
    }
    public VisitLoglet(String name, String ipField, String dateField) {
        super(name);
        this.ipField = ipField;
        this.dateField = dateField;
        this.fieldNames = new String[] { dateField, ipField, "logevent" };
    }

    public String[] getFieldNames() {
        return this.fieldNames;
    }

    public boolean hasMoreEvents(LogIterator logIt) {
        return logIt.hasNext() || !queueMap.isEmpty();
    }

    public LogEvent parseEvent(LogIterator logIt) throws LogViewException {
        String ip;
        String lastDate;

        if(queueMap.isEmpty()) {
            // get next event
            LogEvent event = logIt.nextLogEvent();
            ip = event.get(this.ipField).getValue().toString();
            lastDate = event.get(this.dateField).getValue().toString();
            lastDate = StringUtils.getPrechomp(lastDate, ":");

            SimpleLogEvent newIpEvent = new SimpleLogEvent();
            ArrayList events = new ArrayList();
            events.add(event);
            newIpEvent.set(new SimpleLogField("logevent", LogTypes.LOGEVENT, events));
            newIpEvent.set(new SimpleLogField(this.dateField, LogTypes.DATE, lastDate));
            newIpEvent.set(new SimpleLogField(this.ipField, LogTypes.IP, ip));
            queueMap.put(ip, lastDate);
            // what to put in here as the value??? used to be MultiMap
            currentIpMap.put(ip, newIpEvent);
        } else {
            ip = (String)queueMap.get(0);
            lastDate = (String)queueMap.get(ip);
        }
//        System.err.println("Considering: "+ip+","+lastDate);

        if(!logIt.hasNext()) {
            // iterator finished, return from cache
            LogEvent event = (LogEvent)currentIpMap.get(ip);
            currentIpMap.remove(ip);
            queueMap.remove(0);
            return event;
        }

        while(true) {
            if(!logIt.hasNext()) {
                // iterator empty, start to empty cache
                LogEvent event = (LogEvent)currentIpMap.get(ip);
                currentIpMap.remove(ip);
                queueMap.remove(0);
                return event;
            }

            LogEvent event = logIt.nextLogEvent();
            String tmpIp = event.get(this.ipField).getValue().toString();
            String tmpDate = event.get(this.dateField).getValue().toString();
            boolean sameVisit = equalsEpsilon(lastDate, tmpDate);
            tmpDate = StringUtils.getPrechomp(tmpDate, ":");

            if(ip.equals(tmpIp)) {
                if(!sameVisit) {
                    // return the entry in the currentIpMap
                    // remove from head of queue map
                    // remove from currentIpMap
                    SimpleLogEvent ipEvent = (SimpleLogEvent)currentIpMap.get(ip);
                    ArrayList events = new ArrayList();
                    events.add(event);
                    SimpleLogEvent newIpEvent = new SimpleLogEvent();
                    newIpEvent.set(new SimpleLogField("logevent", LogTypes.LOGEVENT, events));
                    newIpEvent.set(new SimpleLogField(this.dateField, LogTypes.DATE, tmpDate));
                    newIpEvent.set(new SimpleLogField(this.ipField, LogTypes.IP, ip));
                    currentIpMap.remove(ip);
                    queueMap.remove(0);
                    queueMap.put(ip, tmpDate);
                    currentIpMap.put(ip, newIpEvent);
                    return ipEvent;
                } else {
                    // add event onto its list of events for this visit
                    SimpleLogEvent ipEvent = (SimpleLogEvent)currentIpMap.get(ip);
                    ArrayList events = (ArrayList)ipEvent.get("logevent").getValue();
                    events.add(event);
                }
            } else {
                // different ip. put in the map properly for the future
                SimpleLogEvent ipEvent;
                ArrayList events;
                if(!queueMap.containsKey(tmpIp)) {
                    ipEvent = new SimpleLogEvent();
                    events = new ArrayList();
                    ipEvent.set(new SimpleLogField("logevent", LogTypes.LOGEVENT, events));
                    ipEvent.set(new SimpleLogField(this.dateField, LogTypes.DATE, tmpDate));
                    ipEvent.set(new SimpleLogField(this.ipField, LogTypes.IP, tmpIp));
                    queueMap.put(tmpIp, tmpDate);
                    currentIpMap.put(tmpIp, ipEvent);
                } else {
                    ipEvent = (SimpleLogEvent)currentIpMap.get(tmpIp);
                    events = (ArrayList)ipEvent.get("logevent").getValue();
                    // should be checking if its the sameVisit and somehow 
                    // creating a new one on the end...
                    // so queueMap needs to be able to have duplicate keys. 
                }
                events.add(event);

                // is this new diff log outside of our epsilon?
                // if so then we assume log is sorted and skip out
                if(!sameVisit) {
                    ipEvent = (SimpleLogEvent)currentIpMap.get(ip);
                    currentIpMap.remove(ip);
                    queueMap.remove(0);
                    return ipEvent;
                }
            }
        }
    }

    // very simple to begin with
    // is it on the same day.
    // date1 has already been prechomped
    public boolean equalsEpsilon(String date1, String date2) {
        String day1 = date1;
        String day2 = StringUtils.getPrechomp(date2, ":");
        return day1.equals(day2);
    }

}
