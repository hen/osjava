package com.generationjava.simuchron;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

import org.osjava.oscube.util.FactoryUtils;

public class AlgorithmFactory {

    public static Algorithm getAlgorithm(Config config, Session session) {
        String name = config.getString("algorithm.name");
        return getAlgorithm(name, config, session);
    }

    static Algorithm getAlgorithm(String name, Config config, Session session) {
        Algorithm algorithm = (Algorithm) FactoryUtils.getObject(name, "Algorithm", "com.generationjava.simuchron");

        if(algorithm == null) {
            throw new RuntimeException("Unable to find algorithm: "+name);
        }

        if(config.has("algorithm.pre.name")) {
            Pre pre = (Pre) FactoryUtils.getObject((String) config.get("algorithm.pre.name"), "Pre", "com.generationjava.simuchron");
            algorithm = new PreAlgorithm( algorithm, pre );
        }

        if(config.has("algorithm.post.name")) {
            Post post = (Post) FactoryUtils.getObject((String) config.get("algorithm.post.name"), "Post", "com.generationjava.simuchron");
            algorithm = new PostAlgorithm( algorithm, post );
        }

        return algorithm;
    }
}
