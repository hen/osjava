package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public class ReportRunner {

    public static void main(String[] args) throws IOException {
        String reportName = args[0];
        String rendererName = args[1];

        ReportRunner runner = new ReportRunner();
        runner.exec(reportName, rendererName);
    }

    public void exec(String reportName, String rendererName) throws IOException {
        // ask user which report they want
        Report report = ReportFactory.getReport(reportName);

        // does report require parameters?

        // what renderers does the report work with?

        // which renderer does user want?
        Renderer renderer = ReportFactory.getRenderer(rendererName);

        // bang
        Writer out = new PrintWriter(System.out);
        renderer.display( report, out);
        out.close();
    }

}

