package com.generationjava.scrape;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class HtmlScraperTest extends TestCase {

    public final String TEST_PAGE = "<html><body><table bgcolor=\"ffffff\"><tr><td align='center'>FOO</td></tr></table></body></html>";

    public HtmlScraperTest(String name) {
        super(name);
    }

    //-----------------------------------------------------------------------
    // To test: 
    //   HtmlScraper()->scrape(..)->get/move etc

    public void testScrape() {
        HtmlScraper scraper = new HtmlScraper();
        scraper.scrape(TEST_PAGE);
        assertEquals( "FOO", scraper.get("td") );
    }

    public void testScrapeAttribute() {
        HtmlScraper scraper = new HtmlScraper();
        scraper.scrape(TEST_PAGE);
        assertEquals( "ffffff", scraper.get("table[bgcolor]") );
    }

    public void testCaseInsensitive() {
        HtmlScraper scraper = new HtmlScraper();
        scraper.scrape(TEST_PAGE);
        assertEquals( "FOO", scraper.get("TD") );
        assertEquals( "FOO", scraper.get("Td") );
        assertEquals( "FOO", scraper.get("tD") );
        assertEquals( "FOO", scraper.get("td") );
    }
    
    public void testCaseInsensitiveAttribute() {
        HtmlScraper scraper = new HtmlScraper();
        scraper.scrape(TEST_PAGE);
        assertEquals( "ffffff", scraper.get("TABLE[bgcolor]") );
        assertEquals( "ffffff", scraper.get("TABLE[BGCOLOR]") );
        assertEquals( "ffffff", scraper.get("table[BGCOLOR]") );
        assertEquals( "ffffff", scraper.get("table[bgcolor]") );
    }

    public void testSingleQuoteAttributes() {
        HtmlScraper scraper = new HtmlScraper();
        scraper.scrape(TEST_PAGE);
        assertEquals( "center", scraper.get("td[align]") );
    }

}
