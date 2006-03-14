package org.osjava.multidoc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.map.ListOrderedMap;

public class DocumentSite {

    private String url;
    private String title;
    private String stylesheet;
    private Map documents = ListOrderedMap.decorate(new HashMap());

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

    public Document getDocument(String name) {
        return (Document) this.documents.get(name);
    }

    public Collection getNames() {
        return this.documents.keySet();
    }

    public void addDocument(String name, Document document) {
        this.documents.put(name, document);
    }

    public String toString() {
        return "["+title+" "+url+" "+stylesheet+"];"+documents;
    }

}

