<%@ page isErrorPage="true" %>
<%@ include file="header.inc" %>
<%@ page import="java.io.*" %>
<%@ page import="org.osjava.reportrunner.*" %>
<%@ page import="org.osjava.reportrunner.servlets.*" %>

<%
    String groupName = request.getParameter(ReportRunnerServlet.GROUP);
    String reportName = request.getParameter(ReportRunnerServlet.REPORT);
    Report report = ReportFactory.getReport(groupName, reportName);
%>

<p>There's been an error, please mail it to <%= report.getAuthor() %>.</p>
<pre>
<%
StringWriter sw = new StringWriter();
PrintWriter pw = new PrintWriter(sw);
exception.printStackTrace(pw);
out.print(sw);
sw.close();
pw.close();
%>
</pre>

<%@ include file="footer.inc" %>
