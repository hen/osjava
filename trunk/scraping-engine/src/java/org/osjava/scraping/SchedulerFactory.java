package org.osjava.scraping;

import org.osjava.scraping.util.FactoryUtils;
import java.util.HashMap;

public class SchedulerFactory {

    static private HashMap cache = new HashMap();

    static public Scheduler getScheduler(Config cfg, Session session) {
        String name = cfg.getString("scheduler");
        if(name == null) {
            name = "Simple";
        }
        if(SchedulerFactory.cache.containsKey(name)) {
            return (Scheduler)SchedulerFactory.cache.get(name);
        }

        Scheduler scheduler = (Scheduler)FactoryUtils.getObject(name, "Scheduler");
        if(scheduler == null) {
            throw new RuntimeException("Unable to find Scheduler. ");
        }
        SchedulerFactory.cache.put(name, scheduler);
        return scheduler;
    }

}
