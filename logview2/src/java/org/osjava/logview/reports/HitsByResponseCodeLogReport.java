package org.osjava.logview.reports;

import org.osjava.reportrunner.*;
import java.util.*;

public class HitsByResponseCodeLogReport extends ApacheLogReport {

    private static Map formattedMap = new HashMap();

    static {
        formattedMap.put("100", "Code 100 - Continue");
        formattedMap.put("101", "Code 101 - Switching Protocols");
        formattedMap.put("200", "Code 200 - OK");
        formattedMap.put("201", "Code 201 - Created");
        formattedMap.put("202", "Code 202 - Accepted");
        formattedMap.put("203", "Code 203 - Non-Authoritative Information");
        formattedMap.put("204", "Code 204 - No Content");
        formattedMap.put("205", "Code 205 - Reset Content");
        formattedMap.put("206", "Code 206 - Partial Content");
        formattedMap.put("300", "Code 300 - Multiple Choices");
        formattedMap.put("301", "Code 301 - Moved Permanently");
        formattedMap.put("302", "Code 302 - Moved Temporarily");
        formattedMap.put("303", "Code 303 - See Other");
        formattedMap.put("304", "Code 304 - Not Modified");
        formattedMap.put("305", "Code 305 - Use Proxy");
        formattedMap.put("400", "Code 400 - Bad Request");
        formattedMap.put("401", "Code 401 - Unauthorized");
        formattedMap.put("402", "Code 402 - Payment Required");
        formattedMap.put("403", "Code 403 - Forbidden");
        formattedMap.put("404", "Code 404 - Not Found");
        formattedMap.put("405", "Code 405 - Method Not Allowed");
        formattedMap.put("406", "Code 406 - Not Acceptable");
        formattedMap.put("407", "Code 407 - Proxy Authentication Required");
        formattedMap.put("408", "Code 408 - Request Time-Out");
        formattedMap.put("409", "Code 409 - Conflict");
        formattedMap.put("410", "Code 410 - Gone");
        formattedMap.put("411", "Code 411 - Length Required");
        formattedMap.put("412", "Code 412 - Precondition Failed");
        formattedMap.put("413", "Code 413 - Request Entity Too Large");
        formattedMap.put("414", "Code 414 - Request-URL Too Large");
        formattedMap.put("415", "Code 415 - Unsupported Media Type");
        formattedMap.put("500", "Code 500 - Server Error");
        formattedMap.put("501", "Code 501 - Not Implemented");
        formattedMap.put("502", "Code 502 - Bad Gateway");
        formattedMap.put("503", "Code 503 - Out of Resources");
        formattedMap.put("504", "Code 504 - Gateway Time-Out");
        formattedMap.put("505", "Code 505 - HTTP Version not supported");
    }

    Map map = new HashMap();

    public void addToReport(String[] line) {
        Integer count = (Integer) map.get(line[7]);
        if(count == null) {
            count = new Integer(1);
            map.put( line[7], count );
        } else {
            map.put( line[7], new Integer( count.intValue() + 1) );
        }
    }

    public Result executeReport() {
        List codes = new ArrayList(map.size());
        List counts = new ArrayList(map.size());
        Iterator iterator = map.keySet().iterator();
        while(iterator.hasNext()) {
            String key = (String) iterator.next();
            codes.add( format(key) );
            counts.add( map.get(key) );
        }
        return new ArrayResult( new Object[] { codes.toArray(), counts.toArray() } );
    }

    public String format(String key) {
        String formatted = (String) formattedMap.get(key);
        if(formatted == null) {
            return key;
        } else {
            return formatted;
        }
    }
}
