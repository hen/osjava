<%@ include file="header.inc" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
%>

<div class="stages"><a href="list_groups.jsp">choose-group</a> -&gt; <a href="list_reports.jsp?<%= ReportRunnerServlet.GROUP %>=<%= groupName %>">choose-report</a></div>

<%
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);
    Report report = ReportFactory.getReport(groupName, reportName);
%>

<div class="feedback">
<p>You have chosen the <span class="chosen-data"><%= report.getLabel() %></span> report from the <span class="chosen-data"><%= report.getReportGroup().getLabel() %></span> report group. </p>
</div>

<div class="input">
<p>This report requires user input; would you please fill out the following information: </p>

<form action="checkparameters">
<input type="hidden" name="<%= ReportRunnerServlet.REPORT %>" value="<%= reportName %>">
<input type="hidden" name="<%= ReportRunnerServlet.GROUP %>" value="<%= groupName %>">
<table>
<%
    Param[] params = report.getParams();
    for(int i=0; i<params.length; i++) {
        Choice[] choices = report.getParamChoices(params[i]);
        if(choices == null) {
            if(java.util.Date.class.isAssignableFrom(params[i].getType())) {
%>

<!-- Calendar widget header -->
<style type="text/css">@import url(jscalendar/calendar-blue.css);</style>
<script type="text/javascript" src="jscalendar/calendar.js"></script>
<script type="text/javascript" src="jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript" src="jscalendar/calendar-setup.js"></script>
<!-- End of Calendar widget header -->
  <!-- Calendar widget -->
  <tr><td><label for="<%= params[i].getName() %>"><%= params[i].getLabel() %></label></td><td><input type="text" id="<%= params[i].getName() %>" name="<%= params[i].getName() %>" />
  <!-- TODO: Make this either button or input-button so it can be on the tab order -->
  <img src="jscalendar/img.gif" id="trigger_<%= params[i].getName() %>" style="cursor: pointer; border: 1px solid red;" title="Date selector"
      onmouseover="this.style.background='red';" onmouseout="this.style.background=''" /><br>
<script type="text/javascript">
  Calendar.setup(
    {
      inputField  : "<%= params[i].getName() %>",
      ifFormat    : "%m/%d/%Y",
      button      : "trigger_<%= params[i].getName() %>"
    }
  );
</script></td></tr>
   <!-- End of Calendar widget -->

<%
            } else {
%>
    <tr><td><label for="<%= params[i].getName() %>"><%= params[i].getLabel() %></label></td><td><input type="text" name="<%= params[i].getName() %>"></td></tr>
<%
            }
        } else {
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
    }
%>
<tr><td colspan="2"><input type="submit" value="Continue"></td></tr>
</table>
</form>
</div>
<%@ include file="footer.inc" %>
