package com.generationjava.simuchron;

import java.util.Date;

import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;

// needs genericizing. TimeOfDayMatcher??
public class BlockPre implements Pre {

    // ??
    public BlockPre() {
    }

    public Date pre(Date ts, Config config, Session session) {
        if(ts.getMinutes() < 30) {
            return null;
        } else {
            return ts;
        }
    }

}
