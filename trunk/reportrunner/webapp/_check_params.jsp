<%@ page import="org.osjava.reportrunner.*" %>
<%
    String reportName = request.getParameter("_report");
    Report report = ReportFactory.getReport(reportName);
    Param[] params = report.getParams();
    for(int i=0; i<params.length; i++) {
        Parser parser = params[i].getParser();
        if(parser != null) {
            parser.parse( request.getParameter(params[i].getName()) );
        } 
    }
    // redirect to choose renderer page
    response.sendRedirect("list_renderers.jsp?"+request.getQueryString());
%>
