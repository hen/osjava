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
