package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public interface Report {

    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    String getLabel();
    void setLabel(String label);
    Renderer[] getRenderers();
    void setRenderers(String renderers);
    Result execute();
    Param[] getParams();
    void addParam(Param param);
    Choice[] getParamChoices(Param param);
    Column[] getColumns();
    void addColumn(Column column);
    void setReportGroup(ReportGroup group);
    ReportGroup getReportGroup();
    String[] getResourceNames();

}
