/*
 * Copyright (c) 2003, Henri Yandell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or 
 * without modification, are permitted provided that the 
 * following conditions are met:
 * 
 * + Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * 
 * + Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * 
 * + Neither the name of OSJava nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software 
 *   without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.osjava.oscube.container;

import org.apache.commons.lang.StringUtils;

import java.util.Date;

import org.quartz.impl.StdSchedulerFactory;
import org.quartz.JobDetail;
import org.quartz.JobDataMap;
import org.quartz.Job;
//import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.CronTrigger;
import org.quartz.SimpleTrigger;

import java.text.ParseException;

import org.apache.log4j.Logger;

import com.generationjava.config.Config;

/// BEWARE: Name clash. Scheduler and org.quartz.Scheduler
public class QuartzScheduler implements Scheduler {

    private static Logger logger = Logger.getLogger(QuartzScheduler.class);

    private org.quartz.Scheduler quartz;

    public QuartzScheduler() {
        try {
            quartz = new StdSchedulerFactory().getScheduler();
            quartz.start();
        } catch(SchedulerException se) {
            throw new RuntimeException("Failed to schedule job. " + se);
        }
    }

    public void schedule(Config cfg, Session session, Runner runner) {
        // TODO: Should this clone, or the driver of this?
        cfg = cfg.cloneConfig();
        String schedule = cfg.getString("schedule");

        // set default
        if(StringUtils.isEmpty(schedule)) {
            schedule = "startup";
        } 

        logger.debug("SCHEDULE: "+schedule);

        // this should be one of the following:
        // a cron-like line or a 'startup' option 
        if("startup".equalsIgnoreCase(schedule)) { 
            Scheduler sch = new SimpleScheduler();
            sch.schedule(cfg, session, runner);
        } else {
            // assume it's a cron statement
            try {
                String ctxt = cfg.getContext();
                String jobName = ctxt+"job";
                // TODO: Make this 'Scraper' or some such. Maybe 
                // a name that links to the current engine's instance
                // Engine: +engine.toString()
                String jobgroup = ctxt+"group"; 
                JobDetail detail = new JobDetail(jobName, jobgroup, QuartzJob.class);
                JobDataMap map = detail.getJobDataMap();
                map.put("cfg", cfg);
                map.put("session", session);
                map.put("runner", runner);

                Trigger trigger = null;
                if("cron".equalsIgnoreCase(schedule)) {
                    String cronTxt = cfg.getString("schedule.cron");
                    logger.debug("Creating cron trigger: "+cronTxt+" for "+ctxt);
                    CronTrigger cron = new CronTrigger(ctxt+"crontrigger", jobgroup, jobName, jobgroup, cronTxt);
                    trigger = cron;
                } else {
                    // TODO: ie) "simple" in this case. need to make this explicit
//                    Date start = cfg.getDate("start");
//                    Date end = cfg.getDate("end");
                    int repeat = cfg.getInt("schedule.repeat");
                    if(repeat == -1) {
                        repeat = SimpleTrigger.REPEAT_INDEFINITELY;
                    }
                    int interval = cfg.getInt("schedule.interval");
                    logger.debug("Creating simple trigger: "+interval+":"+repeat+" for "+ctxt);
                    SimpleTrigger simp = new SimpleTrigger(ctxt+"simpletrigger", jobgroup, repeat, interval);
                    trigger = simp;
                }

                quartz.scheduleJob(detail, trigger);
            } catch(ParseException pe) {
                throw new RuntimeException("Failed to parse cron. " + pe);
            } catch(SchedulerException se) {
                throw new RuntimeException("Failed to schedule job. " + se);
            }
        }
    }

}
