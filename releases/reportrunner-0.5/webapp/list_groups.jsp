<%@ include file="header.inc" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<div class="input">
<p>What kind of report would you like to run? </p>

<table>
<%
    ReportGroup[] groups = ReportFactory.getReportGroups();
    for(int i=0; i<groups.length; i++) {
%>
    <tr><td><a href="list_reports.jsp?<%= ReportRunnerServlet.GROUP %>=<%= groups[i].getName() %>"><%= groups[i].getLabel() %></a></td><td><%= groups[i].getDescription() %></td></tr>
<%
    }
%>
</table>
</div>
<%@ include file="footer.inc" %>
