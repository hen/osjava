package org.osjava.reportrunner.formatters;

import org.osjava.reportrunner.*;

public class NullFormatter extends AbstractFormatter {

    public Object format(Object input) {
        if(input == null) {
            return getPattern();
        } else {
            return input;
        }
    }

}
