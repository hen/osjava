<%@ include file="header.inc" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
%>

<div class="feedback">
<p>You have chosen the <%= groupName %> group. </p>
</div>

<div class="input">
<p>Select a report: </p>

<table>
<%
    Report[] reports = ReportFactory.getReports(groupName);
    for(int i=0; i<reports.length; i++) {
%>
    <tr><td><a href="choosereport?<%= ReportRunnerServlet.GROUP %>=<%= groupName %>&<%= ReportRunnerServlet.REPORT %>=<%= reports[i].getName() %>"><%= reports[i].getLabel() %></a></td></tr>
<%
    }
%>
</table>
</div>
<%@ include file="footer.inc" %>
