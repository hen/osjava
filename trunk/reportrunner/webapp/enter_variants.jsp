<%@ include file="header.inc" %>
<%@ page import="java.util.*" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);
    Report report = ReportFactory.getReport(groupName, reportName);
    Variant[] variants = report.getVariants();
%>

<div class="stages"><a href="list_groups.jsp">choose-group</a> -&gt; <a href="list_reports.jsp?<%= ReportRunnerServlet.GROUP %>=<%= groupName %>">choose-report</a> -&gt; 
<%
  if(ChooseReportServlet.hasResourceChoice(report, request)) {
%>
<a href="enter_resource_params.jsp?<%= request.getQueryString() %>">choose resource</a>
<% 
  } else {
%>
<s>choose resource</s>
<% 
  }
%>
</div>

<div class="feedback">
<p>You have chosen the <span class="chosen-data"><%= report.getLabel() %></span> report from the <span class="chosen-data"><%= report.getReportGroup().getLabel() %></span> report group. </p>
<p>The report will run against the following resources: </p>
<table>
<%
    String[] names = report.getResourceNames();
    for(int i=0; i<names.length; i++) {
        String value = request.getParameter(names[i]);
        if(value == null) {
            value = names[i];
        }
        %>
        <tr><td><span class="chosen-data"><%= report.getReportGroup().getResource(value).getLabel() %></span></td></tr>
        <%
    }
%>
</table>
</div>

<div class="input">
<p>This report may be run against the following resources; would you please select from the following: </p>

<form action="choosereport">
<%= ReportRunnerServlet.parametersToHiddens(request, variants) %>
<input type="hidden" name="y" value="1">
<table>
<%
    for(int i=0; i<variants.length; i++) {
        VariantOption[] options = variants[i].getOptions();
%>
    <tr><td><label for="<%= variants[i].getName() %>"><%= variants[i].getLabel() %></label></td><td><select name="<%= variants[i].getName() %>">
<%
          for(int j=0; j<options.length; j++) {
%>
              <option value="<%= options[j].getName() %>"><%= options[j].getLabel() %></option>
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
