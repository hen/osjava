package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public interface Post {

    public Number post(Number num, Date ts, Config config, Session session);

}
