package org.osjava.reportrunner.formatters;

import org.osjava.reportrunner.*;

import java.text.*;

public class DateTimeFormatter extends AbstractFormatter {

    public Object format(Object input) {
        if( ! (input instanceof java.util.Date) ) {
            throw new IllegalArgumentException("Type must be java.util.Date or a subclass. ");
        }
        SimpleDateFormat sdf = new SimpleDateFormat( getPattern() );
        return sdf.format( (java.util.Date) input);
    }

}
