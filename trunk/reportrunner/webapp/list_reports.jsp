<%@ page import="org.osjava.reportrunner.*" %>

<p>Select a report: </p>

<table>
<%
    Report[] reports = ReportFactory.getReports();
    for(int i=0; i<reports.length; i++) {
%>
    <tr><td><a href="choosereport?_report=<%= reports[i].getName() %>"><%= reports[i].getLabel() %></a></td></tr>
<%
    }
%>
</table>
