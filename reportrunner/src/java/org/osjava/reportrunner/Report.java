package org.osjava.reportrunner;

import java.io.*;
import java.util.*;
import com.generationjava.io.xml.*;
import com.generationjava.lang.*;

public interface Report {

    String getName();
    void setName(String name);
    String getLabel();
    void setLabel(String label);
    String getRenderers();
    void setRenderers(String renderers);
    Object[] execute();
    Param[] getParams();
    void addParam(Param param);
    Choice[] getParamChoices(Param param);

}
