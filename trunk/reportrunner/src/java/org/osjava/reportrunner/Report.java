package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public interface Report {

    String getReportName();
    void setReportName(String name);
    String getReportLabel();
    void setReportLabel(String label);
    String getRenderers();
    void setRenderers(String renderers);
    Object[] execute();
    Param[] getParams();
    void addParam(Param param);
    Choice[] getParamChoices(Param param);

}
