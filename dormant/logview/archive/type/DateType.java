package com.generationjava.logview.type;

import java.util.Date;

public class DateType extends AbstractType {

    /**
     * Is obj1 equal to obj2, within a given error value epsilon.
     *
     * ie) 10/5/2001 17:48 and 10/5/2001 18:02 and epsilon :20 are equal
     */
    public boolean equals(Object obj1, Object obj2, Object epsilon) {
        if(obj1 instanceof Date) {
            if(obj2 instanceof Date) {
                if(epsilon instanceof Date) {
                }
            }
        }
        return false;
    }

}
