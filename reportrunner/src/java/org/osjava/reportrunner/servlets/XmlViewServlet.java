package org.osjava.reportrunner.servlets;

import java.io.IOException;
import javax.servlet.http.*;
import org.osjava.reportrunner.*;

public class XmlViewServlet extends HttpServlet {

    public static final String GROUP = "_group";
    public static final String REPORT = "_report";

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String groupName = request.getParameter(GROUP);
        String reportName = request.getParameter(REPORT);

        String xml = ReportFactory.getReportAsXml(groupName, reportName);

        // prepare response
        response.setContentType( "text/xml" );
//        response.setHeader("Content-Disposition", "attachment; filename="+report.getName()+".xml");

        response.getWriter().write(xml);
        response.getWriter().flush();

    }
}
