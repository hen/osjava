package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class MaxAlgorithm implements Algorithm {

    public Number evaluate(Date ts, Config config, Session session) {
        return new Integer(1);
    }


}
