package org.osjava.logview.reports;

import java.io.*;
import java.util.*;

import org.osjava.reportrunner.*;
import org.osjava.logview.text.*;

import org.apache.commons.lang.math.NumberUtils;
import java.text.SimpleDateFormat;
import java.text.ParseException;

// TODO: Rename to SummaryLogReport
/**
 * Categorically NOT threadsafe. 
 */
public class TotalLogReport extends ApacheLogReport {

    public static long VISIT_INTERVAL = 30 * 60 * 1000;
    
    private static SimpleDateFormat fmt = new SimpleDateFormat("dd/MMM/yyyy:hh:mm:ss Z");

    private static Column[] columns = Column.createColumns( new String[] {
            "Hits",
            "Files",
            "Pages",
            "Visits",
            "Bytes",
            "Unique Sites",
            "Unique Urls",
            "Unique Referrers",
            "Unique User Agents",
            "Average hits per hour",
            "Average hits per day",
            "Average files per day",
            "Average pages per day",
            "Average visits per day",
            "Average bytes per day",
            "Max hits per hour",
            "Max hits per day",
            "Max files per day",
            "Max pages per day",
            "Max visits per day",
            "Max bytes per day",
        } );

    // for unique counts
    private Set siteSet = new HashSet();
    private Set urlSet = new HashSet();
    private Set referrerSet = new HashSet();
    private Set userAgentSet = new HashSet();

    // for counts/averages/maxes
    private StatCounter hitsPerHour  = new StatCounter();
    private StatCounter hitsPerDay   = new StatCounter();
    private StatCounter filesPerDay  = new StatCounter();
    private StatCounter pagesPerDay  = new StatCounter();
    private StatCounter visitsPerDay = new StatCounter();
    private StatCounter bytesPerDay  = new StatCounter();

    private String lastDay = null;
    private String lastHour = null;

    // used to calculate visits
    private Map visitMap = new HashMap();

    // ip, login, username, time, method, url, protocol, status, bytes, referrer, user-agent
    public void addToReport(String[] line) {

        // BUG: fill in missing days/hours. Averages are off otherwise.
        // Get timestamp in Date form
        // look at lastDate, find hour difference or day difference
        // and call endChunk that number of times

        // uses temporary string hack :- fails when daylight saving happens

        // chop minutes onwards off
        String hour = line[3].substring(0, 13);

        // chop hours onwards off
        String day = line[3].substring(0, 10);

        // check if it's a new hour
        if(!hour.equals(lastHour)) {
            lastHour = hour;
            hitsPerHour.endChunk();
        }
        
        // check if it's a new day
        if(!day.equals(lastDay)) {
            lastDay = day;
            hitsPerDay.endChunk();
            filesPerDay.endChunk();
            pagesPerDay.endChunk();
            visitsPerDay.endChunk();
            bytesPerDay.endChunk();
        }

        // hit occurred
        hitsPerHour.add(1);
        hitsPerDay.add(1);

        // file occurred
        if("200".equals(line[7])) {
            filesPerDay.add(1);
        }

        // page occurred
        // TODO: Needs to improve to strip off the query parameters
        // TODO: Consider having "/$" be a page
        if( line[5].endsWith(".html") ||
            line[5].endsWith(".htm") ||
            line[5].endsWith(".cgi") )
        {
            pagesPerDay.add(1);

            Date lastHit = (Date) visitMap.get(line[0]);
            Date thisHit = convert(line[3]);
            // visit occurred
            if(lastHit == null ||
               thisHit.getTime() - lastHit.getTime() > VISIT_INTERVAL)
            {
                visitsPerDay.add(1);
                visitMap.put(line[0], thisHit);
            }
        }

        // bytes occurred
        bytesPerDay.add( NumberUtils.stringToInt(line[8]) );

        siteSet.add(line[0]);
        urlSet.add(line[5]);
        referrerSet.add(line[9]);
        userAgentSet.add(line[10]);
    }

    public Result executeReport() {
        hitsPerHour.endChunk();
        hitsPerDay.endChunk();
        filesPerDay.endChunk();
        pagesPerDay.endChunk();
        visitsPerDay.endChunk();
        bytesPerDay.endChunk();

        return new ArrayResult( columns, new Object[] { new Object[] { 
            new Integer(hitsPerDay.count()),
            new Integer(filesPerDay.count()),
            new Integer(pagesPerDay.count()),
            new Integer(visitsPerDay.count()),
            new Integer(bytesPerDay.count()),
            new Integer(siteSet.size()),
            new Integer(urlSet.size()),
            new Integer(referrerSet.size()),
            new Integer(userAgentSet.size()),
            new Double(hitsPerHour.average()),
            new Double(hitsPerDay.average()),
            new Double(filesPerDay.average()),
            new Double(pagesPerDay.average()),
            new Double(visitsPerDay.average()),
            new Double(bytesPerDay.average()),
            new Integer(hitsPerHour.max()),
            new Integer(hitsPerDay.max()),
            new Integer(filesPerDay.max()),
            new Integer(pagesPerDay.max()),
            new Integer(visitsPerDay.max()),
            new Integer(bytesPerDay.max()),
        } } );
    }

    private static Date convert(String dateStr) {
        try {
            // 07/Mar/2005:23:36:01 -0500
            return fmt.parse(dateStr);
        } catch(ParseException pe) {
            pe.printStackTrace();
            return null;
        }
    }

}

class StatCounter {

    // the total of the current chunk
    private int runningTotal;

    // the total of all chunks so far
    private int chunksTotal;

    // the number of chunks so far
    private int chunkCount;

    // the current max chunked value
    private int max;

    public void add(int value) {
        runningTotal += value;
    }

    public void endChunk() {
        if(runningTotal > max) {
            max = runningTotal;
        }
        chunksTotal += runningTotal;
        chunkCount++;
        runningTotal = 0;
    }

    public int count() {
        return chunksTotal;
    }

    public int max() {
        return max;
    }

    public double average() {
        if(chunkCount == 0) {
            return 0;
        }
        return round( ( (double) chunksTotal ) / ( (double) chunkCount ), 2);
    }

    private double round(double value, int precision) {
        double valueNum = Math.floor(value);
        double decimal = value - valueNum;
        for(int i=precision; i>0; i--) {
            decimal *= 10;
        }
        decimal = Math.round(decimal);
        for(int i=precision; i>0; i--) {
            decimal /= 10;
        }
        return valueNum + decimal;
    }

}
