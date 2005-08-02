package org.osjava.webwizard;

import org.osjava.reportrunner.*;

import org.apache.velocity.Template;
import org.apache.velocity.servlet.VelocityServlet;
import org.apache.velocity.app.FieldMethodizer;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.ParseErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerServlet extends VelocityServlet {

    // expect to be a url of form:  http://foo.example.com/reports/controller/list_groups.vm
    public Template handleRequest(HttpServletRequest request, HttpServletResponse response, Context ctxt) {
        // get path info such that the vm filename is known
        String file = request.getPathInfo();

        ctxt.put("RRR", new FieldMethodizer( new Constants() ) );

        String groupName = request.getParameter(Constants.GROUP);
        if(groupName == null || "".equals(groupName)) {
            ReportGroup[] groups = ReportFactory.getReportGroups();
            ctxt.put("groups", groups);
        } else {
            ReportGroup group = ReportFactory.getReportGroup(groupName);
            ctxt.put("group", group);

            String reportName = request.getParameter(Constants.REPORT);
            if(reportName == null || "".equals(reportName)) {
                Report[] reports = ReportFactory.getReports(groupName);
                ctxt.put("reports", reports);
            } else {
                Report report = ReportFactory.getReport(groupName, reportName);
                ctxt.put("report", report);
            }
        }

        // if list-groups 

        // if list-reports

        // if enter-resource-params

        // if enter-params

        // if enter-variants

        // if list-renderers

        String template = "/org/osjava/webwizard/templates"+file+".vm";
        try {
            return getTemplate(template);
        } catch( ResourceNotFoundException rnfe ) {
            rnfe.printStackTrace();
            // couldn't find the template
            throw new RuntimeException("Failed to find resource["+template+"]: " + rnfe);
        } catch( ParseErrorException pee ) {
            pee.printStackTrace();
            // syntax error : problem parsing the template
            throw new RuntimeException("Failed to parse resource["+template+"]: " + pee);
        } catch( Exception e ) {
            e.printStackTrace();
            // syntax error : general exception
            throw new RuntimeException("FIX THIS: " + e);
        }
    }

}
