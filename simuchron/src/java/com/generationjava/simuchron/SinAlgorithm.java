package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class SinAlgorithm implements Algorithm {

    public Number evaluate(Date ts, Config config, Session session) {
        double hours = ts.getHours();
        hours +=  ((double)ts.getMinutes())/60;
        double value = Math.sin(2*Math.PI*hours/24);
        return new Double(value);
    }


}
