package org.osjava.multidoc.creators;

import org.osjava.multidoc.*;

import com.generationjava.scrape.HtmlScraper;
import com.generationjava.net.UrlW;
import com.generationjava.web.XmlW;
import com.generationjava.web.HtmlW;

import java.io.IOException;

public class JavadocCreator implements DocumentProjectCreator {

    public DocumentProject create(String urlbase) throws IOException {
        HtmlScraper scraper = new HtmlScraper();
        try {
            scraper.scrape( ""+UrlW.getContent(urlbase+"/overview-summary.html") );
            DocumentProject project = scrapeOverview(scraper, urlbase);
            return project;
        } catch(IOException ioe) {
            // assume it means that overview-summary is not available
            String pckge = ""+UrlW.getContent(urlbase+"/package-list");
            String path = pckge.replaceAll("\\.","/");
            DocumentProject project = new DocumentProject( urlbase, pckge );
            project.addPackage( new DocumentPackage(pckge, "/"+path+"/package-summary.html", "") );
            return project;
        }
    }

    private DocumentProject scrapeOverview(HtmlScraper scraper, String urlbase) throws IOException {
        // bit of a hack. two types of possible comment noticed so far
        if(!scraper.moveToComment("=========== END OF NAVBAR ===========")) {
            scraper.moveToComment("========= END OF TOP NAVBAR =========");
        }

        // also a hack.  TOP variant uses a h1.
        String title = scraper.get("h2").trim();
        if("".equals(title)) {
            title = scraper.get("h1").trim();
        }
        
        // hack for Commons APIs that have <h1> in their title
        title = XmlW.removeXml(title);
        
        // title seems to have ' API' on the end
        title = title.replaceFirst(" API$", "");

        DocumentProject project = new DocumentProject(urlbase, title);

        scraper = scraper.scrapeTag("TABLE");
        scraper.move("TD");  // skip the Packages header
        while(scraper.move("TD")) {
            String cellPackage = scraper.get("A");
            String cellAnchor = scraper.get("A[HREF]");
            if(null == cellAnchor || "".equals(cellAnchor)) { continue; }  // HACK: Bug in scraper I think
            scraper.move("TD");
            String cellDescription = scraper.get("TD").trim();
            project.addPackage( new DocumentPackage( cellPackage, cellAnchor, cellDescription ) );
        }

        return project;
    }

}
