<%@ page import="com.genscape.reports.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%
    String reportName = request.getParameter("report");

%>

<p>You have chosen the <%= reportName %> report. </p>

<p>Select a renderer: </p>

<table>
<%
    Report report = ReportFactory.getReport(reportName);
    String[] renderers = StringUtils.split(report.getRenderers(), ",");
    for(int i=0; i<renderers.length; i++) {
%>
    <tr><td><a href="reportrunner.jsp?<%= request.getQueryString() %>&renderer=<%= renderers[i]%>"><%= renderers[i] %></a></td></tr>
<%
    }
%>
</table>
