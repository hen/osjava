package com.generationjava.logview.report;

import java.util.ArrayList;
import java.util.Iterator;

import com.generationjava.collections.CollectionsW;
import org.apache.commons.lang.StringUtils;

public class TableReport extends AbstractReport {

    private String[] headers;
    private ArrayList table = new ArrayList();
    private ArrayList list;

    public TableReport(Iterator headers) {
        this.headers = (String[])CollectionsW.iteratorToArray(headers, new String[0]);
    }

    public void addField(Object obj) {
        if(list == null) {
            list = new ArrayList();
            table.add(list);
        }
        list.add(obj);
    }

    public void endRow() {
        list = null;
    }

    public Iterator iterator() {
        return table.iterator();
    }

    public String getHeader() {
        return StringUtils.join(this.headers, " : ");
    }

    public String[] getHeaders() {
        return this.headers;
    }

    // deprecated
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(":");
        buffer.append(getHeader());
        buffer.append(":\n");
        buffer.append( StringUtils.repeat("=", getHeader().length()+2 ) );
        buffer.append("\n");
        Iterator iterator = table.iterator();
        while(iterator.hasNext()) {
            Iterator sub = ((ArrayList)iterator.next()).iterator();
            buffer.append(":");
            if(sub.hasNext()) {
                while(sub.hasNext()) {
                    buffer.append(sub.next());
                    buffer.append(":");
                }
            } else {
                buffer.append(":");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

}
