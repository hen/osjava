package org.osjava.reportrunner.reports;

import org.osjava.reportrunner.*;

import java.util.ArrayList;
import java.io.*;

import com.generationjava.io.*;

public class CsvReport extends AbstractReport {

    private String filename = "CsvFile";

    public void setResource(String name, String resourceName) {
        this.filename = resourceName;
    }

    public Result execute() {
        ReportGroup group = super.getReportGroup();
        Resource resource = group.getResource(this.filename);

        try {
            File file = (File) resource.accessResource();
            FileReader rdr = new FileReader(file);
            CsvReader csv = new CsvReader(rdr);
            String[] line = null;
            ArrayList list = new ArrayList();
            while( (line = csv.readLine()) != null ) {
                list.add(line);
            }
            return new ArrayResult( list.toArray() );
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
        return new NullResult();
    }

    public Choice[] getParamChoices(Param param) {
        return null;
    }

    public String[] getResourceNames() {
        return new String[] { "CsvFile" };
    }

}
