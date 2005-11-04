package org.osjava.scripts4java;
import java.util.Arrays;
import java.util.HashSet;

public class SimpleDiffCriteria implements DiffCriteria
{
    public boolean validClass(ClassInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    public boolean validMethod(MethodInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    public boolean validField(FieldInfo info) {
        return !info.isSynthetic() && (info.isPublic() || info.isProtected());
    }
    
    public boolean differs(ClassInfo oldInfo, ClassInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        if (!oldInfo.getSupername().equals(newInfo.getSupername()))
            return true;
        java.util.Set oldInterfaces
            = new HashSet(Arrays.asList(oldInfo.getInterfaces()));
        java.util.Set newInterfaces
            = new HashSet(Arrays.asList(newInfo.getInterfaces()));
        if (!oldInterfaces.equals(newInterfaces))
            return true;
        return false;
    }
    
    public boolean differs(MethodInfo oldInfo, MethodInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        if (oldInfo.getExceptions() == null
            || newInfo.getExceptions() == null) {
            if (oldInfo.getExceptions() != newInfo.getExceptions())
                return true;
        } else {
            java.util.Set oldExceptions
                = new HashSet(Arrays.asList(oldInfo.getExceptions()));
            java.util.Set newExceptions
                = new HashSet(Arrays.asList(newInfo.getExceptions()));
            if (!oldExceptions.equals(newExceptions))
                return true;
        }
        return false;
    }
    
    public boolean differs(FieldInfo oldInfo, FieldInfo newInfo) {
        if (oldInfo.getAccess() != newInfo.getAccess())
            return true;
        if (oldInfo.getValue() == null || newInfo.getValue() == null) {
            if (oldInfo.getValue() != newInfo.getValue())
                return true;
        } else if (!oldInfo.getValue().equals(newInfo.getValue()))
            return true;
        return false;
    }
}
