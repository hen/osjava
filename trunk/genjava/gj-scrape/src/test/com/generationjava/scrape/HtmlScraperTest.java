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
        scraper = null;
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

    public void testScrapeTableCell() {
        scraper.move("tr");
        String t = scraper.get("tr");
        HtmlScraper scraper2 = new HtmlScraper();
        scraper2.scrape(t);
//        HtmlScraper scraper2 = scraper.scrapeTag("tr");
        assertEquals( "<td align='center'>FOO</td>", scraper2.toString() );
        assertTrue(scraper2.move("td"));
        assertEquals("FOO", scraper2.get("td"));
    }

    public void testTmp() {
        HtmlScraper s = new HtmlScraper();
        s.scrape("<td align='center'>FOO</td>");
        assertTrue(s.move("td"));
        assertEquals("FOO", s.get("td"));
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
        assertEquals( "FOO", scraper.get("td") );
        assertFalse( scraper.move("td") );

        scraper.scrape(TEST_PAGE+TEST_PAGE);
        assertTrue( scraper.move("td") );
        assertEquals( "FOO", scraper.get("td") );
        assertTrue( scraper.move("td") );
        assertEquals( "FOO", scraper.get("td") );
        assertFalse( scraper.move("td") );
    }

    public void testScrapeTable() {
        Object[] data = scraper.scrapeTable();
        assertEquals( "FOO", ((Object[])data[0])[0] );
    }

    public void testScrapeTable2() {
        Object[] data = scraper.scrapeTable( new Object[] { String.class } );
        assertEquals( "FOO", ((Object[])data[0])[0] );
    }

}

