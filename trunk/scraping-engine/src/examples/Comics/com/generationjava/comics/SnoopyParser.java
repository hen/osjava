package com.generationjava.comics;

import com.generationjava.scrape.HtmlScraper;

public class SnoopyParser extends UrlScraper {

    protected String scrapeUrl(HtmlScraper scraper) {
        scraper.moveToTagWith("ALT", "Today's Strip");
        scraper.moveToTagWith("WIDTH", "485");
        scraper.moveToTagWith("ALT", "Today's Strip");

        return scraper.get("IMG[SRC]");
    }

}

