package org.osjava.scraping;

import org.osjava.scraping.util.FactoryUtils;

public class NotificationFactory {

    static public Notifier getSuccessNotifier(Config cfg, Session session) {
        String name = cfg.getString("notifier.success");
        Notifier notifier = (Notifier)FactoryUtils.getObject(name, "Notifier");
        if(notifier == null) {
            throw new RuntimeException("Unable to find Success Notifier. ");
        }
        return notifier;
    }

    static public Notifier getErrorNotifier(Config cfg, Session session, Exception e) {
        String name = cfg.getString("notifier.error."+e.getClass().getName());
        if(name == null) {
            name = cfg.getString("notifier.error");
        }
        Notifier notifier = (Notifier)FactoryUtils.getObject(name, "Notifier");
        if(notifier == null) {
            throw new RuntimeException("Unable to find Error Notifier. ");
        }
        return notifier;
    }

}
