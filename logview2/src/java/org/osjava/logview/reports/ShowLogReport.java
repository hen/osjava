package org.osjava.logview.reports;

import org.osjava.reportrunner.*;
import java.util.ArrayList;
import java.util.List;

public class ShowLogReport extends ApacheLogReport {

    private List list = new ArrayList();

    public void addToReport(String[] line) {
        list.add( line );
    }

    public Result executeReport() {
        return new ArrayResult(getLines().toArray());
    }

    protected List getLines() {
        return this.list;
    }

}
