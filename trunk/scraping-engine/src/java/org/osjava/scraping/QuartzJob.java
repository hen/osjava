package org.osjava.scraping;

import org.quartz.JobDetail;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Job;

public class QuartzJob implements Job {

    public QuartzJob() {
    }

    public void execute(JobExecutionContext jec) throws JobExecutionException {
        Scheduler sch = new SimpleScheduler();
        JobDetail jd = jec.getJobDetail();
        JobDataMap map = jd.getJobDataMap();
        Config cfg = (Config)map.get("cfg");
        Session session = (Session)map.get("session");
        Runner runner = (Runner)map.get("runner");
        sch.schedule(cfg, session, runner);
    }

}
