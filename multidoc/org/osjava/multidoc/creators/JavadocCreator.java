package org.osjava.multidoc.creators;

import org.osjava.multidoc.*;

import com.generationjava.scrape.HtmlScraper;
import com.generationjava.net.UrlW;
import com.generationjava.web.XmlW;
import com.generationjava.web.HtmlW;

import java.io.IOException;

public class JavadocCreator implements DocumentCreator {

    public Document create(String urlbase) throws IOException {
        HtmlScraper scraper = new HtmlScraper();
        try {
            scraper.scrape( ""+UrlW.getContent(urlbase+"/overview-summary.html") );
            Document document = scrapeOverview(scraper, urlbase);
            return document;
        } catch(IOException ioe) {
            // assume it means that overview-summary is not available
            String pckge = ""+UrlW.getContent(urlbase+"/package-list");
            String path = pckge.replaceAll("\\.","/");
            Document document = new Document( urlbase, pckge );
            document.setSinglePackage(true);
            document.addPackage( new DocumentPackage(pckge, "/"+path+"/package-summary.html", "") );
            return document;
        }
    }

    private Document scrapeOverview(HtmlScraper scraper, String urlbase) throws IOException {
        boolean xref = false;

        // bit of a hack. two types of possible comment noticed so far
        if(!scraper.moveToComment("=========== END OF NAVBAR ===========")) {
            if(!scraper.moveToComment("========= END OF TOP NAVBAR =========")) {
                // must be an xref page
                xref = true;
                scraper.move("center");
            }
        }

        // also a hack.  TOP variant uses a h1.
        String title = scraper.get("h2").trim();
        if("".equals(title)) {
            title = scraper.get("h1").trim();
        }
        if(xref) {
            // xref has Reference a lot
            title = title.replaceFirst(" Reference$", "");
        } else {
            // title seems to have ' API' on the end
            title = title.replaceFirst(" API$", "");
        }

        Document document = new Document(urlbase, title);

        scraper = scraper.scrapeTag("TABLE");
        scraper.move("TD");  // skip the Packages header
        while(scraper.move("TD")) {
            String cellPackage = scraper.get("A");
            String cellAnchor = scraper.get("A[HREF]");
            if(null == cellAnchor || "".equals(cellAnchor)) { continue; }  // HACK: Bug in scraper I think
            if(xref) {
                document.addPackage( new DocumentPackage( cellPackage, cellAnchor, "") );
            } else {
                scraper.move("TD");
                String cellDescription = scraper.get("TD").trim();
                document.addPackage( new DocumentPackage( cellPackage, cellAnchor, cellDescription ) );
            }
        }

        return document;
    }

}
