<%@ page import="com.genscape.reports.*" %>

<p>Select a report: </p>

<table>
<%
    String[] reports = ReportFactory.getReportNames();
    for(int i=0; i<reports.length; i++) {
%>
    <tr><td><a href="_chose_report.jsp?report=<%= reports[i] %>"><%= reports[i] %></a></td></tr>
<%
    }
%>
</table>
