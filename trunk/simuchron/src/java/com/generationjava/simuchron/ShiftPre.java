package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class ShiftPre implements Pre {

    public Date pre(Date ts, Config config, Session session) {
        // number of hours to shift, in milliseconds
        int n;
        if(config.has("algorithm.pre.hour")) {
            n = config.getInt("algorithm.pre.hour") * 60 * 60 * 1000;
        } else {
            throw new IllegalArgumentException("ShiftPre requires an hour parameter. ");
        }
        long milli = ts.getTime();
        milli += n;
        return new Date(milli);
    }

}
