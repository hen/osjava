package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

public interface Pre {

    public Date pre(Date ts, Config config, Session session);

}
