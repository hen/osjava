package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public class PostAlgorithm implements Algorithm {

    private Algorithm alg;
    private Post post;

    public PostAlgorithm(Algorithm alg, Post post) {
        this.alg = alg;
        this.post = post;
    }

    public Number evaluate(Date ts, Config config, Session session) {
        Number num = this.alg.evaluate( ts, config, session );
        num = this.post.post( num, ts, config, session );
        return num;
    }

}
