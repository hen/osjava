package org.osjava.reportrunner.parsers;

import org.osjava.reportrunner.*;

import java.text.*;

public class DateTimeParser extends AbstractParser {

    public Object parse(String input) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat( getPattern() );
            java.util.Date date = sdf.parse(input);
            if(java.util.Date.class == getType()) {
                return date;
            } else 
            if(java.sql.Date.class == getType()) {
                return new java.sql.Date(date.getTime());
            } else 
            if(java.sql.Time.class == getType()) {
                return new java.sql.Time(date.getTime());
            } else 
            if(java.sql.Timestamp.class == getType()) {
                return new java.sql.Timestamp(date.getTime());
            } else {
                throw new RuntimeException("Illegal type for formatter. ");
            }
        } catch(ParseException pe) {
            return null;
        }
    }

}
