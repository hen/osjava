package org.osjava.multidoc.creators;

import org.osjava.multidoc.*;

import com.generationjava.scrape.HtmlScraper;
import com.generationjava.net.UrlW;
import com.generationjava.web.XmlW;
import com.generationjava.web.HtmlW;

import java.io.IOException;

public class XRefCreator implements DocumentProjectCreator {

    public DocumentProject create(String urlbase) throws IOException {
        HtmlScraper scraper = new HtmlScraper();
        try {
            scraper.scrape( ""+UrlW.getContent(urlbase+"/overview-summary.html") );
            DocumentProject project = scrapeOverview(scraper, urlbase);
            return project;
        } catch(IOException ioe) {
            return null;
        }
    }

    private DocumentProject scrapeOverview(HtmlScraper scraper, String urlbase) throws IOException {
        scraper.move("center");

        // also a hack.  TOP variant uses a h1.
        String title = scraper.get("h2").trim();
        if("".equals(title)) {
            title = scraper.get("h1").trim();
        }
        // xref has Reference a lot
        title = title.replaceFirst(" Reference$", "");

        DocumentProject project = new DocumentProject(urlbase, title);

        scraper = scraper.scrapeTag("TABLE");
        scraper.move("TD");  // skip the Packages header
        while(scraper.move("TD")) {
            String cellPackage = scraper.get("A");
            String cellAnchor = scraper.get("A[HREF]");
            if(null == cellAnchor || "".equals(cellAnchor)) { continue; }  // HACK: Bug in scraper I think
            project.addPackage( new DocumentPackage( cellPackage, cellAnchor, "") );
        }

        return project;
    }

}
