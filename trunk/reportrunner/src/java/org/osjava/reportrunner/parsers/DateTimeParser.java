package org.osjava.reportrunner.parsers;

import org.osjava.reportrunner.*;

import java.text.*;

public class DateTimeParser extends AbstractParser {

    public Object parse(String input, Class type) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( getPattern() );
            java.util.Date date = sdf.parse(input);
            System.err.println("THE TYPE IS: "+type);
            if(type == java.sql.Date.class) {
                return new java.sql.Date(date.getTime());
            } else 
            if(type == java.sql.Time.class) {
                return new java.sql.Time(date.getTime());
            } else 
            if(type == java.sql.Timestamp.class) {
                return new java.sql.Timestamp(date.getTime());
            } else {
                return date;
            }
        } catch(ParseException pe) {
            throw new RuntimeException("Failed parsing: "+getPattern()+" for a "+type+" with input "+input);
        }
    }

}
