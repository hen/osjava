package com.generationjava.comics;

import com.generationjava.scrape.HtmlScraper;
import com.generationjava.web.XmlW;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import org.osjava.scraping.AbstractParser;
import com.generationjava.config.Config;
import org.osjava.scraping.Page;
import org.osjava.oscube.container.Session;
import org.osjava.scraping.ParsingException;
import org.osjava.oscube.container.Result;
import org.osjava.oscube.container.SingleResult;

public abstract class UrlScraper extends AbstractParser {

    public Result parse(Page page, Config cfg, Session session) throws ParsingException {
        HtmlScraper scraper = new HtmlScraper();
        try {
            scraper.scrape(page.readAsString());
        } catch(IOException ioe) {
            throw new ParsingException("Unable to read page. ", ioe);
        }
        String url = scrapeUrl(scraper);
        if(url == null) {
            throw new ParsingException("Unable to find url for : " + cfg.getContext());
        }
        try {
            if(url.indexOf("://") != -1) {
                return new SingleResult( new URL(url) );
            }
            if(url.startsWith("/")) {
                return new SingleResult( new URL( toBase( new URL( page.getDocumentBase() ) ) + url) );
            } else {
                return new SingleResult( new URL(page.getDocumentBase() + "/" + url) );
            }

        } catch(MalformedURLException murle) {
            throw new ParsingException("Unable to parse url: " +url, murle);
        }
    }

    protected abstract String scrapeUrl(HtmlScraper scraper);

    // stolen from AbstractHttpFetcher
    private URL toBase(URL url) throws MalformedURLException {
        return new URL(url.getProtocol() + "://" + url.getHost() + 
            (
                url.getPort() == -1 ? "" : ":" + url.getPort()
            ) + "/");
    }

}

