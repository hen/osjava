package org.osjava.reportrunner;

import org.apache.commons.convert1.ConvertRegistry;
import org.apache.commons.convert1.ConvertUtils;

public class Convert {

    private static ConvertRegistry registry = new ConvertRegistry();

    static { 
        ConvertUtils.registerStandardFromStringConverters(registry);
    }

    public static Object convert(String value, Class type) {
        return registry.convert(value, type);
    }

}
