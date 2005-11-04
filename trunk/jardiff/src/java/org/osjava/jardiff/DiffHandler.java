package org.osjava.scripts4java;

public interface DiffHandler
{
    public void startDiff(String string, String string_0_)
        throws DiffException;
    
    public void startRemoved() throws DiffException;
    
    public void classRemoved(ClassInfo classinfo) throws DiffException;
    
    public void endRemoved() throws DiffException;
    
    public void startAdded() throws DiffException;
    
    public void classAdded(ClassInfo classinfo) throws DiffException;
    
    public void endAdded() throws DiffException;
    
    public void startChanged() throws DiffException;
    
    public void startClassChanged(String string) throws DiffException;
    
    public void fieldRemoved(FieldInfo fieldinfo) throws DiffException;
    
    public void methodRemoved(MethodInfo methodinfo) throws DiffException;
    
    public void fieldAdded(FieldInfo fieldinfo) throws DiffException;
    
    public void methodAdded(MethodInfo methodinfo) throws DiffException;
    
    public void classChanged(ClassInfo classinfo, ClassInfo classinfo_1_)
        throws DiffException;
    
    public void fieldChanged(FieldInfo fieldinfo, FieldInfo fieldinfo_2_)
        throws DiffException;
    
    public void methodChanged
        (MethodInfo methodinfo, MethodInfo methodinfo_3_) throws DiffException;
    
    public void endClassChanged() throws DiffException;
    
    public void endChanged() throws DiffException;
    
    public void endDiff() throws DiffException;
}
