package com.generationjava.logview.type;

import java.net.InetAddress;

public class InetType extends AbstractType {

    /**
     * Is obj1 equal to obj2, within a given error value epsilon.
     *
     * Epsilon can be: 10.*.*.* so that anything inside the 10. 
     * range is considered equal.
     */
    public boolean equals(Object obj1, Object obj2, Object epsilon) {
        if(obj1 instanceof InetAddress) {
            if(obj2 instanceof InetAddress) {
                if(epsilon instanceof InetAddress) {
                }
            }
        }
        return false;
    }

}
