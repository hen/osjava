<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%
    String reportName = request.getParameter("_report");

%>

<p>You have chosen the <%= reportName %> report. </p>

<p>Select a renderer: </p>

<table>
<%
    Report report = ReportFactory.getReport(reportName);
    Renderer[] renderers = report.getRenderers();
    for(int i=0; i<renderers.length; i++) {
%>
    <tr><td><a href="reportrunner?<%= request.getQueryString() %>&_renderer=<%= renderers[i].getName() %>"><%= renderers[i].getLabel() %></a></td></tr>
<%
    }
%>
</table>
