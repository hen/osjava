packagr org.osjava.webwizard;

import org.osjava.reportrunner.*;
import org.apache.velocity.servlet.VelocityServlet;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.Context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerServlet extends VelocityServlet {

    // expect to be a url of form:  http://foo.example.com/reports/velocity/list-groups.vm
    public void handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctxt) {
        // get path info such that the vm filename is known
        String vmFilename = "";

        ctxt.put("RRR", new FieldMethodizer( new Constants() ) );

        // if list-groups
        ReportGroup[] groups = ReportFactory.getReportGroups();
        ctxt.put("groups", groups);

        // if list-reports
        String groupName = request.getParameter(Constants.GROUP);
        ReportGroup group = ReportFactory.getReportGroup(groupName);
        ctxt.put("group", group);
        Report[] reports = ReportFactory.getReports(groupName);
        ctxt.put("reports", reports);

        // if enter-resource-params
        String groupName = request.getParameter(Constants.GROUP);
        ReportGroup group = ReportFactory.getReportGroup(groupName);
        ctxt.put("group", group);
        String reportName = request.getParameter(Constants.REPORT);
        Report report = ReportFactory.getReport(groupName, reportName);
        ctxt.put("report", report);

        // if enter-params

        // if enter-variants

        // if list-renderers

        String template = "org/osjava/webwizard/templates/"+vmFilename;
        try {
            return getTemplate(template);
        } catch( ResourceNotFoundException rnfe ) {
            rnfe.printStackTrace();
            // couldn't find the template
        } catch( ParseErrorException pee ) {
            pee.printStackTrace();
            // syntax error : problem parsing the template
        }
    }

}
