package com.generationjava.comics;

import com.generationjava.scrape.HtmlScraper;

public class UserFriendlyParser extends UrlScraper {

    protected String scrapeUrl(HtmlScraper scraper) {
        scraper.moveToTagWith("ALT", "Latest Strip");

        return scraper.get("IMG[SRC]");
    }

}

