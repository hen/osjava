package org.osjava.scraping;

public class ConfigFactory {

    static public Config getConfig(String[] args) {
        return new JndiConfig();
    }

}
