<%@ page import="com.genscape.reports.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%
    String reportName = request.getParameter("report");

%>

<p>You have chosen the <%= reportName %> report. </p>

<p>It requires the following information: </p>

<form action="list_renderers.jsp">
<input type="hidden" name="report" value="<%= reportName %>">
<%
    Report report = ReportFactory.getReport(reportName);
    if(report instanceof com.genscape.reports.reports.SqlReport) {
        ((com.genscape.reports.reports.SqlReport)report).setDataSource("jdbc/rollerdb");
    }
    Param[] params = report.getParams();
    for(int i=0; i<params.length; i++) {
        Choice[] choices = report.getParamChoices(params[i]);
        if(choices == null) {
%>
    <%= params[i].getName() %>: <input type="text" name="<%= params[i].getName() %>"><br>
<%
        } else {
%>
    <%= params[i].getName() %>: <select name="<%= params[i].getName() %>">
<%
          for(int j=0; j<choices.length; j++) {
%>
              <option value="<%= choices[j].getValue() %>"><%= choices[j].getLabel() %></option>
<%
          }
%>
        </select><br>
<%
        }
    }
%>
<input type="submit" value="Use this information">
</form>
