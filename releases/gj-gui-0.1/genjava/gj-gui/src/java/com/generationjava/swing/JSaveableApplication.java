package com.generationjava.swing;

import java.awt.FileDialog;
import java.awt.event.ActionEvent;

abstract public class JSaveableApplication extends JBasicApplication {

    public final int SAVE = FileDialog.SAVE;

    public JSaveableApplication(String name) {
        super(name);

        registerMenuItem("File","Save",0,"S");
        registerMenuItem("File","Save As",0,"Shift-S");
        registerMenuItem("File","Revert",0,"R");
    }

    abstract protected void fileSaved(String filename, Object obj);
    abstract protected String getCurrentFilename();
    
    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        if("Save".equals(cmd)) {
            String filename = getCurrentFilename();
            if(filename == null) {
                filename = getFileName(SAVE);
                fileSaved(filename, getCurrent());
            }
        } else
        if("Save As".equals(cmd)) {
            String filename = getFileName(SAVE);
            if(filename != null) {
                fileSaved(filename, getCurrent());
            }
        } else
        if("Revert".equals(cmd)) {
            String filename = getCurrentFilename();
            if(filename != null) {
                removeFromOpenList(getCurrent());
                fileClosed(getCurrent());
                Object obj = fileOpened(filename);
                setCurrent(obj);
                selection();
                addToOpenList(obj);
            }
        }
        super.actionPerformed(ae);
    }


}
