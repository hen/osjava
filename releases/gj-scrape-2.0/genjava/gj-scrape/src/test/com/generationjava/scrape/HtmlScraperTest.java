package com.generationjava.scrape;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class HtmlScraperTest extends TestCase {

    public static final String TEST_PAGE = "<html><body><table bgcolor=\"ffffff\"><tr><td align='center'>FOO</td></tr></table></body></html>";

    private HtmlScraper scraper;

    public HtmlScraperTest(String name) {
        super(name);
    }

    public void setUp() {
        scraper = new HtmlScraper();
        scraper.scrape(TEST_PAGE);
    }

    public void tearDown() {
    }

    //-----------------------------------------------------------------------
    // To test: 
    //   HtmlScraper()->scrape(..)->get/move etc

    public void testScrape() {
        assertEquals( "FOO", scraper.get("td") );
    }

    public void testScrapeAttribute() {
        assertEquals( "ffffff", scraper.get("table[bgcolor]") );
    }

    public void testCaseInsensitive() {
        assertEquals( "FOO", scraper.get("TD") );
        assertEquals( "FOO", scraper.get("Td") );
        assertEquals( "FOO", scraper.get("tD") );
        assertEquals( "FOO", scraper.get("td") );
    }
    
    public void testCaseInsensitiveAttribute() {
        assertEquals( "ffffff", scraper.get("TABLE[bgcolor]") );
        assertEquals( "ffffff", scraper.get("TABLE[BGCOLOR]") );
        assertEquals( "ffffff", scraper.get("table[BGCOLOR]") );
        assertEquals( "ffffff", scraper.get("table[bgcolor]") );
    }

    public void testSingleQuoteAttributes() {
        assertEquals( "center", scraper.get("td[align]") );
    }

    public void testScrapeTag() {
        HtmlScraper scraper2 = scraper.scrapeTag("table");
        assertEquals( "<tr><td align='center'>FOO</td></tr>", scraper2.toString() );
    }

    public void testMoveToTagWithTwice() {
        assertTrue( scraper.moveToTagWith("bgcolor", "ffffff") );
        assertFalse( scraper.moveToTagWith("bgcolor", "ffffff") );

        scraper.scrape(TEST_PAGE+TEST_PAGE);
        assertTrue( scraper.moveToTagWith("bgcolor", "ffffff") );
        assertTrue( scraper.moveToTagWith("bgcolor", "ffffff") );
        assertFalse( scraper.moveToTagWith("bgcolor", "ffffff") );
    }

    public void testMove() {
        assertTrue( scraper.move("td") );
        assertFalse( scraper.move("td") );

        scraper.scrape(TEST_PAGE+TEST_PAGE);
        assertTrue( scraper.move("td") );
        assertTrue( scraper.move("td") );
        assertFalse( scraper.move("td") );
    }


}
