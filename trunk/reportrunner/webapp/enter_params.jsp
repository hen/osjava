<%@ include file="header.inc" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);
    Report report = ReportFactory.getReport(groupName, reportName);
    ReportRunnerServlet.applyResources(report, request);
    ChooseReportServlet.applyVariantParams(report, request);
    Param[] params = report.getParams();
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
-&gt;
<%
  if(report.getVariants().length != 0) {
%>
<a href="enter_variants.jsp?<%= request.getQueryString() %>">choose variants</a>
<% 
  } else {
%>
<s>choose variants</s>
<% 
  }
%>
</div>

<div class="feedback">
<p>You have chosen the <span class="chosen-data"><%= report.getLabel() %></span> report from the <span class="chosen-data"><%= report.getReportGroup().getLabel() %></span> report group. </p>
<p>"<%= report.getReportGroup().getDescription() %>"</p>
<p>"<%= report.getDescription() %>"</p>
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
<p>This report requires user input; would you please fill out the following information: </p>

<form action="checkparameters">
<%= ReportRunnerServlet.parametersToHiddens(request, params) %>
<table>
<%
    for(int i=0; i<params.length; i++) {
        Object value = params[i].getDefault();
        if(value == null) {
            value = "";
        } else {
            value = value.toString();
        }

        Choice[] choices = report.getParamChoices(params[i]);
        // HACK: to hide the fact we haven't got a widget plugin system yet
        if("textfield".equals(params[i].getWidget())) {
%>
    <tr><td><label for="<%= params[i].getName() %>"><%= params[i].getLabel() %></label></td><td><input type="text" name="<%= params[i].getName() %>" value="<%= value %>"></td></tr>
<%
        } else
        if(choices == null) {
            if(Boolean.class.isAssignableFrom(params[i].getType())) {
%>
    <tr><td><label for="<%= params[i].getName() %>"><%= params[i].getLabel() %></label></td><td><input type="checkbox" name="<%= params[i].getName() %>" value="<%= value %>"></td></tr>
<%
            } else
            if(java.util.Date.class.isAssignableFrom(params[i].getType())) {
%>

<!-- Calendar widget header -->
<style type="text/css">@import url(jscalendar/calendar-blue.css);</style>
<script type="text/javascript" src="jscalendar/calendar.js"></script>
<script type="text/javascript" src="jscalendar/lang/calendar-en.js"></script>
<script type="text/javascript" src="jscalendar/calendar-setup.js"></script>
<!-- End of Calendar widget header -->
  <!-- Calendar widget -->
  <tr><td><label for="<%= params[i].getName() %>"><%= params[i].getLabel() %></label></td><td><input type="text" id="<%= params[i].getName() %>" name="<%= params[i].getName() %>" value="<%= value %>"/>
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
    <tr><td><label for="<%= params[i].getName() %>"><%= params[i].getLabel() %></label></td><td><input type="text" name="<%= params[i].getName() %>" value="<%= value %>"></td></tr>
<%
            }
        } else {
            String multiple = "";
            if(Object[].class.isAssignableFrom(params[i].getType())) {
                multiple = "multiple=\"multiple\" size=\"30\"";
            }
%>
    <tr><td><label for="<%= params[i].getName() %>"><%= params[i].getLabel() %></label></td><td><select name="<%= params[i].getName() %>"<%= multiple %>>
<%
          for(int j=0; j<choices.length; j++) {
              String selected = "";
              if(value.equals(choices[j].getValue())) {
                  selected = " selected=\"selected\"";
              }
%>
              <option value="<%= choices[j].getValue() %>"<%= selected %>><%= choices[j].getLabel() %></option>
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
