package org.osjava.multidoc;

import java.util.List;
import java.util.ArrayList;

public class DocumentSite {

    private String url;
    private String title;
    private String stylesheet;
    private List documents = new ArrayList();

    public DocumentSite(String url, String title, String stylesheet) {
        this.url = url;
        this.title = title;
        this.stylesheet = stylesheet;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getStylesheet() {
        return this.stylesheet;
    }

    public List getDocuments() {
        return this.documents;
    }

    public void addDocument(Document document) {
        this.documents.add(document);
    }

    public String toString() {
        return "["+title+" "+url+" "+stylesheet+"];"+documents;
    }

}

