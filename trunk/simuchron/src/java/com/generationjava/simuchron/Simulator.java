package com.generationjava.simuchron;

import java.util.Date;
import java.util.ArrayList;

import com.generationjava.config.Config;
import org.osjava.oscube.container.ConfigFactory;
import org.osjava.oscube.container.Session;
import org.osjava.oscube.container.NamespaceSession;

public class Simulator {

    public static void main(String[] args) throws Exception {
        Simulator self = new Simulator();
        Algorithm s = null;
        int code = 0;
        if(args.length != 0) {
            code = Integer.parseInt(args[0]);
        }
//        Algorithm alg = new PostAlgorithm(getAlgorithm(code), new CapacityPost());
        Algorithm alg = getAlgorithm(code);
        int numPeriods = 48;
        Date[] x = new Date[numPeriods];
        long time = System.currentTimeMillis();
        int period = 60*30*1000;  // 30 minutes
        for(int i=0; i<numPeriods; i++) {
            x[i] = new Date(time - i * period);
        }
        Number[] y = self.simulate(alg, x);

        // strip nulls - playing... very hacky
        ArrayList list1 = new ArrayList(y.length);
        ArrayList list2 = new ArrayList(x.length);
        for(int i=0; i<y.length; i++) {
            if(y[i] != null) {
                list1.add(y[i]);
                list2.add(x[i]);
            }
        }
        y = (Number[]) list1.toArray(new Number[0]);
        x = (Date[]) list2.toArray(new Date[0]);

        PlotChart.showTimeGraph(x, y);
    }

    private static Algorithm getAlgorithm(int code) {
        switch(code) {
    /*
            case -1: return new MinAlgorithm();
            case 0: return new MaxAlgorithm();
            case 1: return new SinAlgorithm();
            case 2: return new CosAlgorithm();
            case 3: return new SlopeAlgorithm();
            case 50: 
                return new PreAlgorithm(new CosAlgorithm(), new ShiftPre(12));
            case 51: 
                return new PreAlgorithm(new CosAlgorithm(), new RoundPre(12));
            case 52: 
                return new PreAlgorithm(new CosAlgorithm(), new RoundPre(2));
            case 53: 
                return new PreAlgorithm(new CosAlgorithm(), new RoundPre((double)0.80));
            case 54: 
                return new PreAlgorithm(new CosAlgorithm(), new BlockPre());
            case 60: 
                return new Play1Algorithm();
            case 61: 
                return new Play1iiAlgorithm(new MaxAlgorithm(), new TimeOfDay(15,00), new Play1iiAlgorithm( new ConstantAlgorithm(), new TimeOfDay(16,00), new Play1iiAlgorithm( new ConstantAlgorithm(), new TimeOfDay(17,00), new Play1iiAlgorithm( new ConstantAlgorithm(), new TimeOfDay(18,00), new MaxAlgorithm() ) ) ) );
            case 100: {
                Algorithm[] algs = new Algorithm[] {
                    new MaxAlgorithm(),
                    new SinAlgorithm(),
                    new CosAlgorithm()
                };
                double[] rats = new double[] { 
                    0.97, 
                    0.01,
                    0.02
                };
                Algorithm s = new SharedAlgorithm();
                return new PostAlgorithm( s, new WobblePost() );
            }
    */
            default: return new MinAlgorithm();
        }
    }

    public Number[] simulate(Algorithm algorithm, Date[] x) {
//        Map map = new HashMap();
//        map.put("+capacity", new Double(100));
//        map.put("-capacity", new Double(-10));
        Config config = ConfigFactory.getConfig(null);
        Session session = new NamespaceSession();
        Number[] y = new Number[x.length];
        for(int i=0; i<x.length; i++) {
            y[i] = algorithm.evaluate(x[i], config, session);
        }
        return y;
    }

    /*
    private static Algorithm createAlgorithm(String name) {
        try {
            Class cl = Class.forName(name);
            Object obj = cl.newInstance();
            return (Algorithm) obj;
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    */

}
