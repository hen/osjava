package org.osjava.convert;

import java.util.HashMap;
import com.generationjava.collections.ClassMap;
import com.generationjava.lang.ClassW;

public class Convert {


    private static ClassMap map = new ClassMap(new HashMap());

    static { 
        map.put(java.lang.Number.class,
            new NumberConverter());
    }

    public static Object convert(String value, String type) {
        Class clss = ClassW.getClass(type); 
        Converter converter = (Converter)map.get(clss);
        if(converter != null) {
            return converter.convert(value);
        } else {
            return value;
        }
    }

}
