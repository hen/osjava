<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<p>Select a group: </p>

<table>
<%
    ReportGroup[] groups = ReportFactory.getReportGroups();
    for(int i=0; i<groups.length; i++) {
%>
    <tr><td><a href="list_reports.jsp?<%= ReportRunnerServlet.GROUP %>=<%= groups[i].getName() %>"><%= groups[i].getLabel() %></a></td></tr>
<%
    }
%>
</table>
