package com.generationjava.util;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import com.generationjava.lang.ClassW;

public class RunMain implements Runnable {

    private Class clss;
    private String[] args;
    
    private RunMain(Class clss, String[] args) {
        this.clss = clss;
        this.args = args;
    }
    
    public static void runMain(Class clss, String[] args) {
        RunMain fc = new RunMain(clss, args);
        Thread thread = new Thread(fc);
        thread.start();
    }
    
    public void run() {
        ClassW.callMain(this.clss, this.args);
    }

}
