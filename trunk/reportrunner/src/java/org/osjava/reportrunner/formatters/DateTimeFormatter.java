package org.osjava.reportrunner.formatters;

import org.osjava.reportrunner.*;

import java.lang.reflect.*;
import java.text.*;

public class DateTimeFormatter extends AbstractFormatter {

    public Object format(Object input) {
        if("oracle.sql.TIMESTAMP".equals(input.getClass().getName())) {
            try {
                Method method = input.getClass().getMethod("timestampValue", new Class[0] );
                input = method.invoke( input, new String[0] );
            } catch(NoSuchMethodException nsme) {
                nsme.printStackTrace();
            } catch(IllegalAccessException iae) {
                iae.printStackTrace();
            } catch(IllegalArgumentException iae) {
                iae.printStackTrace();
            } catch(InvocationTargetException ite) {
                ite.printStackTrace();
            }
        }

        if(input instanceof java.util.Date) {
            SimpleDateFormat sdf = new SimpleDateFormat( getPattern() );
            return sdf.format( (java.util.Date) input);
        } else {
            return input;
        }
    }

}
