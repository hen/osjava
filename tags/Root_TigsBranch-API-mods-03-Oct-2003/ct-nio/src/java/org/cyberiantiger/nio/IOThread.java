package org.cyberiantiger.nio;

import java.io.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;

public class IOThread extends Thread {

    private Selector mySelector;

    /**
     * Field containing the boolean value which indicates whether or not the 
     * thread should abort.  This field exists for interoperability with 
     * TigThreads ExtendedRunnable class.
     */
    private volatile boolean abort=false;

    /**
     * Create a new IOThread.
     *
     * @throws IOException If there is a problem creating the Selector
     */
    public IOThread() throws IOException {
        mySelector = Selector.open();
    }

    /**
     * Register a ChannelHandler with this IOThread
     *
     */
    public SelectionKey register(ChannelHandler handler, int ops) throws IOException {
        SelectableChannel chan = handler.getSelectableChannel();

        if (chan.isBlocking()) {
            throw new IllegalArgumentException("SelectableChannel is blocking");
        }

        SelectionKey ret = chan.register(mySelector, ops, handler);

        // XXX: Workaround, Selector hangs when it has nothing registered.
        synchronized (this) {
            notify();
        }
        return ret;
    }

    public void run() {
        while (!isAborting()) {
            try {
                // XXX: Workaround, Selector.select() never returns if
                // there is nothing to select on ! (even if you 
                // call Selector.wakeup() )
                synchronized (this) {
                    try {
                        while (mySelector.keys().size() == 0)
                            wait();
                    } catch (InterruptedException ie) {
                        System.out.println("Breaking out of IOThread");
                        break;
                    }
                }
                try {
                    while (mySelector.select() == 0);
                } catch (InterruptedIOException iie) {
                    System.out.println("Breaking out of IOThread");
                    break;
                }
                Set keys = mySelector.selectedKeys();
                Iterator i = keys.iterator();
                while (i.hasNext()) {
                    SelectionKey key = (SelectionKey)i.next();
                    ChannelHandler handler = (ChannelHandler)key.attachment();
                    if (key.isValid()) {
                        int ops = key.readyOps();
                        if ((ops & SelectionKey.OP_ACCEPT) != 0) {
                            handler.accept();
                        }
                        if ((ops & SelectionKey.OP_CONNECT) != 0) {
                            handler.connect();
                        }
                        if ((ops & SelectionKey.OP_READ) != 0) {
                            handler.read();
                        }
                        if ((ops & SelectionKey.OP_WRITE) != 0) {
                            handler.write();
                        }
                    }
                    i.remove();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        // XXX: This doesn't work !!!!
        // Cleanup
        // This is where we cleanup the mess left when we're interupted
        // TODO: Only close channels if 'closeOnExit' is set.
        Iterator i = mySelector.keys().iterator();
        while (i.hasNext()) {
            SelectionKey key = (SelectionKey)i.next();
            ChannelHandler handler = (ChannelHandler)key.attachment();
            if (key.isValid()) {
                try {
                    handler.close();
                    handler.deregister();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        try {
            mySelector.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Method to set the thread to cease execution gracefully.  This method 
     * exists for interoperability with ExtendedRunnable from the TigThrads 
     * package, but is in no way required.  
     * @param abort boolean value indicating whether or not the thread should
     *              cease execution.
     */
    public void setAbort(boolean abort) {
        this.abort=abort;
    }

    /**
     * Method returning whether or not the thread is in the process of ceasing
     * to run.  This method exists for interoperability with ExtendedRunnable 
     * from the TigThreads package, but is in no way required.
     * @return boolean value indicating whether or  not the thread is in the
     *         process of ceasing execution.
     */
    public boolean isAborting() {
        return abort;
    }
}
