package org.osjava.reportrunner.servlets;

import java.io.IOException;
import javax.servlet.http.*;
import org.osjava.reportrunner.*;

public class ReportRunnerServlet extends HttpServlet {

    public static final String REPORT = "_report";
    public static final String RENDERER = "_renderer";
    public static final String DS_NAME = "ReportRunnerDS";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String reportName = request.getParameter(REPORT);
        String rendererName = request.getParameter(RENDERER);

        // which report they want
        Report report = ReportFactory.getReport(reportName);

        // HACK
        if(report instanceof org.osjava.reportrunner.reports.SqlReport) {
            String dsName = getServletContext().getInitParameter(ReportRunnerServlet.DS_NAME);
            ((org.osjava.reportrunner.reports.SqlReport)report).setDataSource(dsName);
        }

        // does report require parameters?
        Param[] params = report.getParams();
        if(params != null) {
            for(int i=0; i<params.length; i++) {
                Parser parser = params[i].getParser();
                String parameter = request.getParameter(params[i].getName());
                Object value = parameter;
                if(parser != null) {
                    value = parser.parse(parameter, params[i].getType());
                } 
                params[i].setValue(value);
            }
        }

        Renderer renderer = ReportFactory.getRenderer(rendererName);

        // prepare response
        response.setContentType( renderer.getMimeType() );
        if(!renderer.isInline()) {
            response.setHeader("Content-Disposition", "attachment; filename="+report.getName()+"."+renderer.getExtension());
        }

        // render results
        if(renderer != null && report != null) {
            Result result = report.execute();
            result = new FormattingResult(result, report);
            renderer.display( result, report, response.getOutputStream() );
        }

        response.getOutputStream().flush();

    }
}
