package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class PreAlgorithm implements Algorithm {

    private Algorithm alg;
    private Pre pre;

    public PreAlgorithm(Algorithm alg, Pre pre) {
        this.alg = alg;
        this.pre = pre;
    }

    public Number evaluate(Date ts, Config config, Session session) {
        ts = this.pre.pre( ts, config, session );
        // null means don't evaluate. a null value is legal
        if(ts == null) {
            return null;
        }
        Number num = this.alg.evaluate( ts, config, session );
        return num;
    }

}
