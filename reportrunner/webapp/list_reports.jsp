<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<p>Select a report: </p>

<table>
<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
    Report[] reports = ReportFactory.getReports(groupName);
    for(int i=0; i<reports.length; i++) {
%>
    <tr><td><a href="choosereport?<%= ReportRunnerServlet.GROUP %>=<%= groupName %>&<% ReportRunnerServlet.REPORT %>=<%= reports[i].getName() %>"><%= reports[i].getLabel() %></a></td></tr>
<%
    }
%>
</table>
