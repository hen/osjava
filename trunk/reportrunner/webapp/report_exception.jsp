<%@ page isErrorPage="true" %>
<%@ include file="header.inc" %>
<%@ page import="java.io.*" %>
<%@ page import="java.net.*" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);
    Report report = ReportFactory.getReport(groupName, reportName);

String str = request.getRequestURL().toString();
str += "?";
str += URLEncoder.encode(request.getQueryString());
str += "\n\n";
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
exception.printStackTrace(pw);
str += sw.toString();
sw.close();
pw.close();
str = str.replaceAll("\n", "%0A");
%>

<p>There's been an error, please mail it to <%= report.getAuthor() %> by clicking the following 
<a href="mailto:<%= report.getAuthor() %>?subject=[rrr] <%=report.getName()%> exception&body=<%= str %>">link</a>.</p>
<pre>
<%= sw %>
</pre>

<%@ include file="footer.inc" %>
