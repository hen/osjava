package org.osjava.scripts4java;

public interface DiffCriteria
{
    public boolean validClass(ClassInfo classinfo);
    
    public boolean validMethod(MethodInfo methodinfo);
    
    public boolean validField(FieldInfo fieldinfo);
    
    public boolean differs(ClassInfo classinfo, ClassInfo classinfo_0_);
    
    public boolean differs(MethodInfo methodinfo, MethodInfo methodinfo_1_);
    
    public boolean differs(FieldInfo fieldinfo, FieldInfo fieldinfo_2_);
}
