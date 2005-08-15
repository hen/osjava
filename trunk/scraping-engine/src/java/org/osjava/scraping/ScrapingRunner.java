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
package org.osjava.scraping;

import java.util.*;
import org.apache.commons.lang.StringUtils;

import org.osjava.oscube.container.Runner;
import org.osjava.oscube.container.Engine;
import com.generationjava.config.Config;
import org.osjava.oscube.container.Session;
import org.osjava.oscube.container.Result;

import org.osjava.oscube.service.store.*;
import org.osjava.oscube.service.notify.*;

import org.apache.log4j.Logger;

public class ScrapingRunner implements Runner {

    private static Logger logger = Logger.getLogger(ScrapingRunner.class);

    // 'org.osjava.oscube.prefix' needs to equal 'org.osjava.scraper' 
    public static void main(String[] args) {
        Engine engine = new Engine();
        ScrapingRunner runner = new ScrapingRunner();
        engine.run(runner, args);
    }

    public void run(Config cfg, Session session) {

        logger.info("Scraping: "+cfg.getString("uri"));

        try {
            // fetch the uri
            Fetcher fetcher = FetchingFactory.getFetcher(cfg, session);
                
            // throws FetchingException
            String uri = cfg.getString("uri");
            Page page = fetcher.fetch(uri, cfg, session);

            // parse the data
            Parser parser = ParserFactory.getParser(cfg, session);

            // TODO: do a pre-check to see if it should be parsed?
            // This would classically be done to stop a parse from 
            // actually happening. 
            // QUERY: Call this Checker??

            // throws ParsingException
            Result result = parser.parse(page, cfg, session);

            // TODO: do a post-check to see if it should be stored?
            // This would classically be done to convert the output 
            // into something else, prior to storing.
            // QUERY: Call this Converter??

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
