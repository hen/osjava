package com.generationjava.simuchron;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class SharedAlgorithm implements Algorithm {

    public Number evaluate(Date ts, Config config, Session session) {
        List algs = config.getList("algorithm.algorithm");
        List ratiosList = config.getList("algorithm.ratio");
        double[] ratios = doubleArrayFromList(ratiosList);
        // check all 0->1 and add up to 1
        checkRatios(ratios);

        double d = 0.0;
        config.setContext( "algorithm." );
        for(int i=0; i<algs.size(); i++) {
            String name = (String) algs.get(i);
            System.err.println("CTXT: "+config.getContext());
            Algorithm alg = AlgorithmFactory.getAlgorithm(name, config, session);
            Number num = alg.evaluate(ts, config, session);
            d += num.doubleValue() * ratios[i];
        }
        return new Double(d);
    }

    private static double[] doubleArrayFromList(List stringList) {
        int sz = stringList.size();
        double[] array = new double[sz];
        for(int i=0; i<sz; i++) {
            String d = (String) stringList.get(i);
            try {
                array[i] = Double.parseDouble(d);
            } catch(NumberFormatException nfe) {
                throw new IllegalArgumentException("Non-numeric ratio specified: " + d);
            }
        }
        return array;
    }

    private static void checkRatios(double[] ratios) {
        double sum = 0.0;
        for(int i=0; i<ratios.length; i++) {
            if(ratios[i] < 0) {
                throw new IllegalArgumentException("Ratio contains value less than 0: "+ratios[i]);
            }
            if(ratios[i] > 1) {
                throw new IllegalArgumentException("Ratio contains value greater than 1: "+ratios[i]);
            }
            sum += ratios[i];
        }
        if(sum != 1.0) {
            throw new IllegalArgumentException("Ratio does not add up to 1. ");
        }
    }

}
