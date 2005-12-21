<%@ include file="header.inc" %>
<%@ page import="java.util.*" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
%>

<div class="stages"><a href="list_groups.jsp">choose-group</a> -&gt; <a href="list_reports.jsp?<%= ReportRunnerServlet.GROUP %>=<%= groupName %>">choose-report</a></div>

<%
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);
    Report report = ReportFactory.getReport(groupName, reportName);
    Param[] params = report.getReportGroup().getResourceParams();
%>

<div class="feedback">
<p>You have chosen the <span class="chosen-data"><%= report.getLabel() %></span> report from the <span class="chosen-data"><%= report.getReportGroup().getLabel() %></span> report group. </p>
<p>"<%= report.getReportGroup().getDescription() %>"</p>
<p>"<%= report.getDescription() %>"</p>
</div>

<div class="input">
<p>This report may be run against the following resources; would you please select from the following: </p>

<form action="choosereport">
<%= ReportRunnerServlet.parametersToHiddens(request, params) %>
<input type="hidden" name="z" value="1">
<table>
<%
    List list = Arrays.asList(report.getResourceNames());
    for(int i=0; i<params.length; i++) {
        if(!list.contains(params[i].getName())) {
            continue;
        }
        Choice[] choices = report.getReportGroup().getResourceParamChoices(params[i]);
%>
    <tr><td><label for="<%= params[i].getName() %>"><%= params[i].getLabel() %></label></td><td><select name="<%= params[i].getName() %>">
<%
          for(int j=0; j<choices.length; j++) {
%>
              <option value="<%= choices[j].getValue() %>"><%= choices[j].getLabel() %></option>
<%
          }
%>
        </select></td></tr>
<%
    }
%>
<tr><td colspan="2"><input type="submit" value="Continue"></td></tr>
</table>
</form>
</div>
<%@ include file="footer.inc" %>
