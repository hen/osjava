package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

// hard coded attempt at problem 1
public class Play1Algorithm implements Algorithm {

    public Number evaluate(Date ts, Config config, Session session) {
        double hours = ts.getHours();
        double mins = ts.getMinutes();
        if(hours < 15 || hours > 18) {
            return new Integer(1);
        }
        if(hours == 16) {
            return new Integer(0);
        }
        if(hours == 15) {
            return new Float((60-mins)/60);
        }
        if(hours == 17) {
            return new Float(mins/60);
        }
        return new Integer(1);   // should be impossible
    }


}
