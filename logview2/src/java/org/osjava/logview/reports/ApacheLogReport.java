package org.osjava.logview.reports;

import java.io.*;
import java.util.*;

import org.osjava.reportrunner.*;
import org.osjava.logview.text.*;

import org.apache.commons.io.IOUtils;

public abstract class ApacheLogReport extends AbstractReport {

    private String resourceName;

    public void setResource(String name, String resourceName) {
        this.resourceName = resourceName;
System.out.println(""+this.resourceName);
    }

    public Result execute() {
        
        // get File
        ReportGroup group = super.getReportGroup();
        // TODO: Fix bug in which this.resourceName is not set to logfile
        Resource resource = group.getResource("logfile");

        FileReader rdr = null;

        // ip, login, username, time, method, url, protocol, status, bytes, referrer, user-agent
        RegexpParser dlp = new RegexpParser("([^ ]*) ([^ ]*) ([^ ]*) \\[([^\\]]*)\\] \"([^ ]*) ([^ ]*) ([^\"]*)\" ([0-9]*) ([0-9-]*) \"([^\"]*)\" \"([^\"]*)\"");

        // read a line at a time, parsing into an Object[] with a parser
        int count = 0;
        try {
            count++;
            File file = (File) resource.accessResource();
            rdr = new FileReader(file);
            BufferedReader bfr = new BufferedReader(rdr);
            String line = null;
            while( (line = bfr.readLine()) != null ) {
                addToReport( dlp.parse(line) );
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        } finally {
            IOUtils.closeQuietly(rdr);
        }

        return executeReport();
    }

    public abstract void addToReport(String[] fields);
    public abstract Result executeReport();

    public Choice[] getParamChoices(Param param) {
        return null;
    }

    public String[] getResourceNames() {
        return new String[] { "logfile" };
    }

}
