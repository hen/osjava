package org.osjava.webwizard;

import org.osjava.reportrunner.*;

import org.osjava.reportrunner.servlets.ReportRunnerServlet;

import javax.servlet.http.HttpServletRequest;

public class ReportRunnerTool {

    public void applyResources(Report report, HttpServletRequest request) {
        ReportRunnerServlet.applyResources(report, request);
    }

    public boolean resourcesChosen(Report report, HttpServletRequest request) {
        return ReportRunnerServlet.resourcesChosen(report, request);
    }

    public String parametersToHiddens(HttpServletRequest request, Nameable[] ignore) {
        return ReportRunnerServlet.parametersToHiddens(request, ignore);
    }

    public void applyVariantParams(Report report, HttpServletRequest request) {
        ReportRunnerServlet.applyVariantParams(report, request);
    }

    public boolean hasResourceChoice(Report report, HttpServletRequest request) {
        return ReportRunnerServlet.hasResourceChoice(report, request);
    }

}
