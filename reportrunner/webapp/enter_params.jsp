<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%
    String reportName = request.getParameter("_report");

%>

<p>You have chosen the <%= reportName %> report. </p>

<p>It requires the following information: </p>

<form action="_check_params.jsp">
<input type="hidden" name="_report" value="<%= reportName %>">
<%
    Report report = ReportFactory.getReport(reportName);
    if(report instanceof org.osjava.reportrunner.reports.SqlReport) {
        ((org.osjava.reportrunner.reports.SqlReport)report).setDataSource("jdbc/rollerdb");
    }
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
  <%= params[i].getLabel() %>:  <input type="text" id="<%= params[i].getName() %>" name="<%= params[i].getName() %>" />
  <!-- TODO: Make this either button or input-button so it can be on the tab order -->
  <img src="jscalendar/img.gif" id="trigger_<%= params[i].getName() %>" style="cursor: pointer; border: 1px solid red;" title="Date selector"
      onmouseover="this.style.background='red';" onmouseout="this.style.background=''" /><br>
<script type="text/javascript">
  Calendar.setup(
    {
      inputField  : "<%= params[i].getName() %>",     // ID of the input field
      ifFormat    : "%m/%d/%Y", // the date format
      button      : "trigger_<%= params[i].getName() %>",  // trigger for the calendar (button ID)
    }
  );
</script>
   <!-- End of Calendar widget -->

<%
            } else {
%>
    <%= params[i].getLabel() %>: <input type="text" name="<%= params[i].getName() %>"><br>
<%
            }
        } else {
%>
    <%= params[i].getLabel() %>: <select name="<%= params[i].getName() %>">
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
