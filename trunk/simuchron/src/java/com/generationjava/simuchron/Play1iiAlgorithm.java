package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

// generic attempt at problem 1
// basically it appears to be an if-else statement
public class Play1iiAlgorithm implements Algorithm {

    private Algorithm a;
    private Algorithm b;
    private TimeOfDay test;

    public Play1iiAlgorithm(Algorithm a, TimeOfDay test, Algorithm b) {
        this.a = a;
        this.b = b;
        this.test = test;
    }

    public Number evaluate(Date ts, Config config, Session session) {
        TimeOfDay t = new TimeOfDay(ts);
        if(t.asMinutes() < this.test.asMinutes()) {
            return this.a.evaluate(ts, config, session);
        } else {
            return this.b.evaluate(ts, config, session);
        }
    }

}
