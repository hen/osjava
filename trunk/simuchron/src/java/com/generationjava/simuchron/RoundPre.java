package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

// rounds to the nearest nth hour
// creates square graphs
public class RoundPre implements Pre {

    public Date pre(Date ts, Config config, Session session) {
        int n = 0;
        if(config.has("algorithm.pre.minute")) {
            n = (int) config.getDouble("algorithm.pre.minute");
        } else 
        if(config.has("algorithm.pre.hour")) {
            n = config.getInt("algorithm.pre.hour") * 60;
        } else {
            throw new IllegalArgumentException("Illegal to not round to an hour or minute. ");
        }

        // if 17:42, and I want to round to the nearest multiple of 180, 
        // it should round to 18:00.
        int minute = ts.getMinutes() + ts.getHours()*60;
        int adjust = minute - roundToNearestMultiple( minute, n );
        long milli = ts.getTime();
        milli = milli - adjust * 60000;
        return new Date(milli);
    }

    private static int roundToNearestMultiple( int x, int n) {
//        System.err.print("ROUNDING: "+x+" to nearest mult of "+n);
        int mod = x % n;
        if(mod >= (float) n / 2) {
            x += (n-mod);
        } else {
            x -= mod;
        }
//        System.err.println("="+x);
        return x;
    }

}
