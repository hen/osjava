package org.osjava.convert;

import org.apache.commons.lang.NumberUtils;

public class NumberConverter implements Converter {

    public Object convert(String value) {
        return NumberUtils.createNumber(value);
    }

}
