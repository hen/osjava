package org.osjava.multidoc.creators;

import org.osjava.multidoc.*;

import com.generationjava.scrape.HtmlScraper;
import com.generationjava.net.UrlW;
import com.generationjava.web.XmlW;
import com.generationjava.web.HtmlW;

import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class JCoverageCreator implements DocumentProjectCreator {

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

        // JCoverage has no title, so let's use the urlbase
        String title = urlbase.replaceFirst("/[^/]*$","");  //.replaceAll("[^/]*/+","");
        title = StringUtils.substringAfterLast(title, "/");


        DocumentProject project = new DocumentProject(urlbase, title);

        scraper.moveToTagWith("class","reportText");  // skip the Project header
        while(scraper.moveToTagWith("class","reportText")) {
            String cellPackage = scraper.get("a");
            String cellAnchor = scraper.get("a[href]");
            project.addPackage( new DocumentPackage( cellPackage, cellAnchor, "") );
        }

        return project;
    }

}
