<%@ include file="header.inc" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);
    Report report = ReportFactory.getReport(groupName, reportName);
    Param[] params = report.getParams();
%>

<div class="stages"><a href="list_groups.jsp">choose-group</a> -&gt; <a href="list_reports.jsp?<%= ReportRunnerServlet.GROUP %>=<%= groupName %>">choose-report</a> -&gt; 
<% 
  if(params != null && params.length != 0) {
%>
<a href="enter_params.jsp?<%= ReportRunnerServlet.GROUP %>=<%= groupName %>&<%= ReportRunnerServlet.REPORT %>=<%= reportName %>">enter information</a>
<%
  } else {
%>
<s>enter information</s>
<%
  }
%>
</div>

<div class="feedback">
<p>You have chosen the <span class="chosen-data"><%= report.getLabel() %></span> report from the <span class="chosen-data"><%= report.getReportGroup().getLabel() %></span> report group. </p>
<%
    if(params != null && params.length != 0) {
%>
<p>The report will run with the following user-supplied information: </p>
<table>
<%
        for(int i=0; i<params.length; i++) {
            %>
            <tr><td class="chosen-data"><%= params[i].getName() %></td><td class="chosen-data"><%= request.getParameter(params[i].getName()) %></td></tr>
            <%
        }
%>
</table>
<%
    }
%>
<p>The report will run against the following resources: </p>
<ul>
<%
    String[] names = report.getResourceNames();
    for(int i=0; i<names.length; i++) {
        %>
        <li><span class="chosen-data"><%= report.getReportGroup().getResource(names[i]).getLabel() %></span></li>
        <%
    }
%>
</ul>
</div>

<div class="input">

<p>How would you like to view the results of the report? </p>

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
