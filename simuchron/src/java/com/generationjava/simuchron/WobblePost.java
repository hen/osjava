package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class WobblePost implements Post {

    public Number post(Number num, Date ts, Config config, Session session) {
        double value = num.doubleValue();
        value += ((value * 3) % 4 ) - 2;
        return new Double(value);
    }


}
