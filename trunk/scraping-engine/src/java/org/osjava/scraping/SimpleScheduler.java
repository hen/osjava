package org.osjava.scraping;

import org.apache.commons.lang.StringUtils;

public class SimpleScheduler implements Scheduler {

    public void schedule(Config cfg, Session session, Runner runner) {
        // push a thread off
        runner.run(cfg.cloneConfig(), session);
    }

}
