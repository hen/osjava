package com.generationjava.logview.report;

import java.util.Properties;

import com.generationjava.logview.Report;

abstract public class AbstractReport implements Report {

    private Properties links;

    public AbstractReport() {
        links = new Properties();
    }

    public void setLink(String field, String link) {
        links.put(field, link);
    }

    public String getLink(String field) {
        return links.getProperty(field);
    }

}
