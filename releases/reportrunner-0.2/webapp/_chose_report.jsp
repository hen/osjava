<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
    String reportName = request.getParameter("_report");
    Report report = ReportFactory.getReport(reportName);
    if(report.getParams().length != 0) {
        // redirect to choose params page
        response.sendRedirect("enter_params.jsp?_report="+reportName);
    } else {
        // redirect to choose renderer page
        response.sendRedirect("list_renderers.jsp?_report="+reportName);
    }
%>
