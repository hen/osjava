package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class SlopeAlgorithm implements Algorithm {

    public Number evaluate(Date ts, Config config, Session session) {
        double hours = ts.getHours();
        hours +=  ((double)ts.getMinutes())/60;
        if(hours > 12) {
            hours = 24 - hours;
        }
        double value = (12-hours)/12;
        value *= 2;
        value -= 1;
        return new Double(value);
    }


}
