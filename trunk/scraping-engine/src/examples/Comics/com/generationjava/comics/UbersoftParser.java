package com.generationjava.comics;

import com.generationjava.scrape.HtmlScraper;

public class UbersoftParser extends UrlScraper {

    protected String scrapeUrl(HtmlScraper scraper) {
        scraper.moveToTagWith("name", "Comic");

        return scraper.get("IMG[SRC]");
    }

}

