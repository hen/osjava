<%@ include file="header.inc" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);

%>

<div class="feedback">
<p>You have chosen the <%= groupName %>/<%= reportName %> report: </p>
<%
    Report report = ReportFactory.getReport(groupName, reportName);
    Param[] params = report.getParams();
    if(params != null && params.length != 0) {
%>
<p>For this report, you chose the following parameters: </p>
<table class="chosen_parameters">
<%
        for(int i=0; i<params.length; i++) {
            %>
            <tr><td><%= params[i].getName() %></td><td><%= request.getParameter(params[i].getName()) %></td></tr>
            <%
        }
%>
</table>
<%
    }
%>
</div>

<div class="input">

<p>Select a renderer: </p>

<table>
<%
    Renderer[] renderers = report.getRenderers();
    for(int i=0; i<renderers.length; i++) {
%>
    <tr><td><a href="reportrunner?<%= request.getQueryString() %>&<%= ReportRunnerServlet.RENDERER %>=<%= renderers[i].getName() %>"><%= renderers[i].getLabel() %></a></td></tr>
<%
    }
%>
</table>
</div>
<%@ include file="footer.inc" %>
