package com.generationjava.comics;

import org.osjava.scraping.parser.UrlScraper;

import com.generationjava.scrape.HtmlScraper;

public class ApodParser extends UrlScraper {

    protected String scrapeUrl(HtmlScraper scraper) {
        return scraper.get("IMG[SRC]");
    }

}

