package com.generationjava.convert;

import org.apache.commons.lang.NumberUtils;

public class Convert {


    public static Object convert(String value, String type) {
        if("number".equals(type)) {
            return NumberUtils.createNumber(value);
        } else {
            return value;
        }
    }

}
