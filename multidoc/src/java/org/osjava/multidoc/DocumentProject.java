package org.osjava.multidoc;

import java.util.List;
import java.util.ArrayList;

public class DocumentProject {

    private String url;
    private String title;
    private List packages = new ArrayList();
//    private boolean singlePackage;

    public DocumentProject(String url, String title) {
        this.url = url;
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List getPackages() {
        return this.packages;
    }

    public void addPackage(DocumentPackage pckge) {
        this.packages.add(pckge);
    }

    public boolean isSinglePackaged() {
        return packages.size() == 1;
    }

    /*
    public void setSinglePackage(boolean b) {
        this.singlePackage = b;
    }
    */

    public String toString() {
        return "["+title+" "+url+(isSinglePackaged()?"/single":"")+"];"+packages;
    }

}

