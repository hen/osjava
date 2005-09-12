package org.osjava.reportrunner.formatters;

import org.osjava.reportrunner.*;

import java.text.*;

public class DateTimeFormatter extends AbstractFormatter {

    public Object format(Object input) {
        if(input instanceof java.util.Date) {
            if("EPOCH".equals(getPattern())) {
                return ""+ ((java.util.Date) input).getTime();
            }
            SimpleDateFormat sdf = new SimpleDateFormat( getPattern() );
            return sdf.format( (java.util.Date) input);
        } else {
            return input;
        }
    }

}
