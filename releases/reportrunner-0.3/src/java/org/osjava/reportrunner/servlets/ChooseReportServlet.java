package org.osjava.reportrunner.servlets;

import java.io.IOException;
import java.util.*;
import javax.servlet.http.*;
import org.osjava.reportrunner.*;

public class ChooseReportServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String groupName = request.getParameter(ReportRunnerServlet.GROUP);
        String reportName = request.getParameter(ReportRunnerServlet.REPORT);
        Report report = ReportFactory.getReport(groupName, reportName);

        if(hasResourceChoice(report, request) && (request.getParameter("z") == null || request.getParameter("z").equals("") ) ) {
            // redirect to choose resources page
            response.sendRedirect("enter_resource_params.jsp?"+request.getQueryString());
            return;
        } 

        if(report.getParams().length != 0) {
            // redirect to choose params page
            response.sendRedirect("enter_params.jsp?"+request.getQueryString());
        } else {
            // redirect to choose renderer page
            response.sendRedirect("list_renderers.jsp?"+request.getQueryString());
        }
    }

    public static boolean hasResourceChoice(Report report, HttpServletRequest request) {
        List list = Arrays.asList(report.getResourceNames());
        Param[] resourceParams = report.getReportGroup().getResourceParams();
        for(int i=0; i<resourceParams.length; i++) {
            if(list.contains(resourceParams[i].getName())) {
                return true;
            }
        }
        return false;
    }
}
