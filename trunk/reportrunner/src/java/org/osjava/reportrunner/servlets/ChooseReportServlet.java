package org.osjava.reportrunner.servlets;

import java.io.IOException;
import javax.servlet.http.*;
import org.osjava.reportrunner.*;

public class ChooseReportServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String groupName = request.getParameter(ReportRunnerServlet.GROUP);
        String reportName = request.getParameter(ReportRunnerServlet.REPORT);
        Report report = ReportFactory.getReport(groupName, reportName);
        if(report.getParams().length != 0) {
            // redirect to choose params page
            response.sendRedirect("enter_params.jsp?"+ReportRunnerServlet.GROUP+"="+groupName+"&"+ReportRunnerServlet.REPORT+"="+reportName);
        } else {
            // redirect to choose renderer page
            response.sendRedirect("list_renderers.jsp?"+ReportRunnerServlet.GROUP+"="+groupName+"&"+ReportRunnerServlet.REPORT+"="+reportName);
        }
    }

}
