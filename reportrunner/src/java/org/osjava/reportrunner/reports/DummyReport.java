package org.osjava.reportrunner.reports;

import org.osjava.reportrunner.*;

public class DummyReport extends AbstractReport {

    public Result execute() throws ReportException {
        return new ArrayResult(
          new Object[] { 
            new Object[] { "1", "2", "3" },
            new Object[] { "4", "2", "3" },
            new Object[] { "5", "2", "3" },
            new Object[] { "6", "2", "3" },
          }
        );
    }

    public Choice[] getParamChoices(Param param) {
        return null;
    }
}
