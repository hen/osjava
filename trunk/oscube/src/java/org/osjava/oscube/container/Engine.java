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
 * + Neither the name of Scabies nor the names of its contributors 
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

import java.util.List;
import java.io.Reader;

public class Engine {

    public static void main(String[] args) throws Exception {
        Engine engine = new Engine();
        // load the config
        Config cfg = ConfigFactory.getConfig(args);
        String runStr = cfg.getString("org.osjava.oscube.runner");
        Class cl = Class.forName(runStr);
        Runner runner = (Runner) cl.newInstance();
        engine.run(runner, args);
    }

/// TODO: Implement the Scheduler aspect
///  The Scheduler notifies only this class. It is 
///  then up to this Engine to run the parsers.
/// TODO: Put the oscube.container and db in a different thread
    public void run(Runner runner, String[] args) {
        // load the config
        Config cfg = ConfigFactory.getConfig(args);

        // get the prefix for this instance, this is the active 
        // object which will be run by the runner
        String prefix = cfg.getString("org.osjava.oscube.prefix");

        // test and how schedule=startup will be handled
        List list = cfg.getList(prefix);
        for(int i=0; i<list.size(); i++) {
            String key = (String)list.get(i);

            Session session = new NamespaceSession();
            session.put(prefix, key);
            cfg.setContext(key+".");

            // schedule the times to run parsers
            // TODO: allow this to be pluggable.
            Scheduler scheduler = SchedulerFactory.getScheduler(cfg, session);
            // Engine suddenly becomes a part of the system. 
            // Possibly the run(Config, Session) needs to 
            // move into an interface
            scheduler.schedule(cfg, session, runner);
        }
    }

}
