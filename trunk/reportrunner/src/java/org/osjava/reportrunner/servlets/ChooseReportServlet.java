package org.osjava.reportrunner.servlets;

import java.io.IOException;
import javax.servlet.http.*;
import org.osjava.reportrunner.*;

public class ChooseReportServlet extends HttpServlet {

    public static final String REPORT = "_report";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String reportName = request.getParameter(REPORT);
        Report report = ReportFactory.getReport(reportName);
        if(report.getParams().length != 0) {
            // redirect to choose params page
            response.sendRedirect("enter_params.jsp?_report="+reportName);
        } else {
            // redirect to choose renderer page
            response.sendRedirect("list_renderers.jsp?_report="+reportName);
        }
    }

}
