package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class ConstantAlgorithm implements Algorithm {

    public Number evaluate(Date ts, Config config, Session session) {
        double d = config.getDouble("algorithm.constant");
        if(d > 1) throw new IllegalArgumentException("Constant must be <= 1");
        if(d < -1) throw new IllegalArgumentException("Constant must be >= -1");
        return new Float(d);
    }


}
