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
 * + Neither the name of Simple-JNDI nor the names of its contributors 
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
package org.osjava.scraping;

import java.util.List;
import java.io.Reader;

public class Engine implements Runner {

    public static void main(String[] args) {
        Engine engine = new Engine();
        engine.run(args);
    }

/// TODO: Implement the Scheduler aspect
///  The Scheduler notifies only this class. It is 
///  then up to this Engine to run the parsers.
/// TODO: Put the scraping and db in a different thread
    public void run(String[] args) {
        // load the config
        Config cfg = ConfigFactory.getConfig(args);


        // test and how schedule=startup will be handled
        List list = cfg.getList("org.osjava.scrapers");
        for(int i=0; i<list.size(); i++) {
            String key = (String)list.get(i);

            Session session = new NamespaceSession();
            session.put("org.osjava.scraper", key);
            cfg.setContext(key+".");

//            String scheduler = cfg.getString("scheduler");

            // schedule the times to run parsers
            // TODO: allow this to be pluggable.
            Scheduler scheduler = SchedulerFactory.getScheduler(cfg, session);
            // Engine suddenly becomes a part of the system. 
            // Possibly the run(Config, Session) needs to 
            // move into an interface
            scheduler.schedule(cfg, session, this);

            // temporary running
//            run(cfg, session);
        }
    }

// From here down is the execution part of a scraper. Runner maybe.
    public void run(Config cfg, Session session) {

        try {
            // fetch the uri
            Fetcher fetcher = FetchingFactory.getFetcher(cfg, session);
                
            // throws FetchingException
            String uri = cfg.getString("uri");
            Page page = fetcher.fetch(uri, cfg, session);

            // parse the data
            Parser parser = ParserFactory.getParser(cfg, session);
            // throws ParsingException
            Result result = parser.parse(page, cfg, session);

            // store the data
            Store store = StoreFactory.getStore(cfg, session);

            // throws StoringException
            store.store(result, cfg, session);

            // notify parties
            Notifier notifier = NotificationFactory.getSuccessNotifier(cfg, session);
            notifier.notify(cfg, session);
        } catch(FetchingException fe) {
            notifyError(cfg, session, fe);
        } catch(ParsingException pe) {
            notifyError(cfg, session, pe);
        } catch(StoringException se) {
            notifyError(cfg, session, se);
        } catch(NotificationException ne) {
            notifyError(cfg, session, ne);
        }
    }

    private static void notifyError(Config cfg, Session session, Exception e) {
        try {
            Notifier notifier = NotificationFactory.getErrorNotifier(cfg, session, e);
            session.put(cfg.getContext()+".error", e);
            notifier.notify(cfg, session);
            session.remove(cfg.getContext()+".error");
        } catch(NotificationException ne) {
            ne.printStackTrace();
        }
    }

}
