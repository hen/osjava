package com.generationjava.apps.jpe;

/**
* ask interface is used as a callback system for input dialog handler
* its using a callback so i don't have to use a extra thread
*/
interface AskInterface {
        public void askCallback(String command,String result);
}
