<%@ page isErrorPage="true" %>
<%@ include file="header.inc" %>
<%@ page import="java.io.*" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);
    Report report = ReportFactory.getReport(groupName, reportName);
%>

The report has returned no data. 

<%@ include file="footer.inc" %>
