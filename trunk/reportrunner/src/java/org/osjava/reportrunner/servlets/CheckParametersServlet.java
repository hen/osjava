package org.osjava.reportrunner.servlets;

import java.io.IOException;
import javax.servlet.http.*;
import org.osjava.reportrunner.*;

public class CheckParametersServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String groupName = request.getParameter(ReportRunnerServlet.GROUP);
        String reportName = request.getParameter(ReportRunnerServlet.REPORT);
        Report report = ReportFactory.getReport(groupName, reportName);
        Param[] params = report.getParams();
        for(int i=0; i<params.length; i++) {
            Parser parser = params[i].getParser();
            if(parser != null) {
                parser.parse( request.getParameter(params[i].getName()), params[i].getType() );
            } 
        }
        // redirect to choose renderer page
        response.sendRedirect("list_renderers.jsp?"+request.getQueryString());
    }

}
