package com.generationjava.apps.jpe;

import java.io.FileDescriptor;
import java.security.Permission;

public class SecManager extends SecurityManager
{
        private boolean checkAccept = false;
        private boolean checkAccess = false;
        private boolean checkConnect = false;
        private boolean checkDelete = false;
        private boolean checkExec = true;
        private boolean checkExit = true;
        private boolean checkLink = false;
        private boolean checkListen = false;
        private boolean checkPackageAccess = false;
        private boolean checkPackageDefinition = false;
        private boolean checkPropertiesAccess = false;
        private boolean checkRead = false;    
        private boolean checkReadFile = false;
        private boolean checkWrite = false;
        private boolean checkWriteFile = false;
        private boolean checkSystemClipboardAccess = false;

        JPE jpe;

        SecManager(JPE jpe)
          {                
                this.jpe=jpe;
        }

    public void checkPermission(Permission perm) {
    }

// rest of these are not needed now probably

        public void checkSystemClipboardAccess() {
        }

        
        /**
          * Checks to see if a socket connection to the specified port on the specified host has been accepted.
         */
    public void checkAccept(String host, int port) {
        }

        /**
          * Checks to see if the specified Thread is allowed to modify the Thread group.
          */ 
        public void checkAccess(Thread g) {
        }

        /**
          * Checks to see if the specified Thread group is allowed to modufy this group.
         */ 
        public void checkAccess(ThreadGroup g) {
        }
   
        /**
          * Checks to see if a socket has connected to the specified port on the specified host. 
           */
        public void checkConnect(String host, int port) {
        }

        /**
         * Checks to see if the current execution contexst and the indicated execution context are both
         * allowed to connect to the indicated host and port.
         */
    public void checkConnect(String host, int port, Object context) {
        }

        /**
         * Checks to see if the ClassLoader has been created.
         */
        public void checkCreateClassLoader() {
        }
  
        /**
         * Checks if the delete()-call comes from the delete-module 
         */
        public void checkDelete(String file) {
        }

        /**
          * Checks if the exec()-call comes from the exec-module 
         */
    public void checkExec(String cmd) {
    }
   
        /**
           * Checks to see if the system has exited the virutal machine with an exit code. 
         */
    public void checkExit(int status) {
                if (jpe.wantexit!=true) {
                        throw new SecurityException("checkExit("+status+") not allowed."); 
                }
        }

        /**
          * Checks to see if the specified linked library exists. 
         */
    public void checkLink(String lib) {
        }

        /**
         * Checks to see if a server socket is listing to the specified local port that it is bounded to.        
         */
    public void checkListen(int port) {
        }                
   
        /**
          * Checks to see if an applet can access a package. 
         */ 
        public void checkPackageAccess(String pkg) {
        }

        /**
         * Check to see if an applet can define classes in a package.
         */
    public void checkPackageDefinition(String pkg) {
        }

        /**
         * Checks to see who has access to the System properties.
         */
    public void checkPropertiesAccess() {
        }

        /**
         * Checks to see who has access to the System properties names by key.
         */
    public void checkPropertyAccess(String key) {
        }
        
        /**
         * Checks to see who has access to the System properties names by key and def.
         */
    public void checkPropertyAccess(String key, String def) {
        }

        /**
         * Checks to see if an input file with the specified file descriptor object gets created. 
         */
    public void checkRead(FileDescriptor fd) {
        }

        /**
         * Checks to see if an input file with the specified system dependent file name gets created. 
         */
    public void checkRead(String file) {
        }

        /**
         * Checks to see if the current context or the indicated context are both allowed to read the
          * given filename.
         */
    public void checkRead(String file, Object context) {
        }

        /**
         * Checks to see if an applet can set a networking-related object factory. 
         */
    public void checkSetFactory() {
        }

        /**
         * Checks to see if an output file with the specified file descriptor object gets created.
         */
    public void checkWrite(FileDescriptor fd) {
        }

        /**
         * Checks to see if an output file with the specified system dependent file name gets created. 
         */
    public void checkWrite(String file) {
        }

        public boolean checkTopLevelWindow(Object window) {
                return(true);
        }

        public void checkMemberAccess(Class cl, int w) {
        }
}        
