<%@ page import="com.genscape.reports.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
    String reportName = request.getParameter("report");
    Report report = ReportFactory.getReport(reportName);
    if(report.getParams().length != 0) {
        // redirect to choose params page
        response.sendRedirect("enter_params.jsp?report="+reportName);
    } else {
        // redirect to choose renderer page
        response.sendRedirect("list_renderers.jsp?report="+reportName);
    }
%>
