package org.osjava.nio;

import java.io.IOException;
import java.io.InterruptedIOException;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

public class IOThread extends Thread {

    private Selector mySelector;

    /* Map for adding ops.  The key is the SelectionKey and the value 
     * is the op to add.
     */
    private Map addOps = new HashMap();
    
    /**
     * Field containing the boolean value which indicates whether or not the 
     * thread should abort.  This field exists for interoperability with 
     * TigThreads ExtendedRunnable class.
     */
    private volatile boolean abort=false;

    private Map removeOps = new HashMap();

    /**
     * Create a new IOThread.
     *
     * @throws IOException If there is a problem creating the Selector
     */
    public IOThread() throws IOException {
       super();
       mySelector = Selector.open();
    }

    /**
     * Create a new IOThread.
     *
     * @param name The name of the thread.
     * @throws IOException If there is a problem creating the Selector
     */
    public IOThread(String name)
        throws IOException {
        super(name);
        mySelector = Selector.open();
    }

    /**
     * Create a new IOThread.
     *
     * @param group the Threadgroup which the created Thread is a member
     * @param name The name of the thread.
     * @throws IOException If there is a problem creating the Selector
     */
    public IOThread(ThreadGroup group, String name)
        throws IOException {
        super(group, name);
        mySelector = Selector.open();
    }
    
   public void addInterestOp(SelectionKey key, int op) {
       int ops = key.readyOps();
       Logger logger = Logger.getLogger(getClass());
       logger.debug("Starting Ops for key '" + key + "' -- ");
       if ((ops & SelectionKey.OP_ACCEPT) != 0) {
           logger.debug("    ACCEPTING");
       }
       if ((ops & SelectionKey.OP_CONNECT) != 0) {
           logger.debug("    CONNECTING");
       }
       if ((ops & SelectionKey.OP_READ) != 0) {
           logger.debug("    READ");
       }
       if ((ops & SelectionKey.OP_WRITE) != 0) {
           logger.debug("    WRITE");
       }
       logger.debug("Op being added -- ");
       if ((op & SelectionKey.OP_ACCEPT) != 0) {
           logger.debug("    ACCEPTING");
       }
       if ((op & SelectionKey.OP_CONNECT) != 0) {
           logger.debug("    CONNECTING");
       }
       if ((op & SelectionKey.OP_READ) != 0) {
           logger.debug("    READ");
       }
       if ((op & SelectionKey.OP_WRITE) != 0) {
           logger.debug("    WRITE");
       }
       if(!mySelector.keys().contains(key)) {
           /* TODO: Make this throw an exception */
           logger.debug("Unknown key.  Aborting.");
           /* Wake up the selector anyway, just in case */
           mySelector.wakeup();
           return;
       }
       if(addOps.containsKey(key)) {
           /* 
            * If the key already exists in the addOps Map, bitwise OR the 
            * value.  Unfortunately this has to be the Integer Object 
            * rather than the primitive, so this looks worse than it 
            * actually is.
            */
           addOps.put(key, new Integer(((Integer)(addOps.get(key))).intValue() | op));
       } else {
           addOps.put(key, new Integer(op));
       } 
       
       if(removeOps.containsKey(key)) {
           removeOps.put(key, new Integer(((Integer)(removeOps.get(key))).intValue() & ~op));
       } 
       logger.debug("Waking up selector");
       mySelector.wakeup();       
   }

   public void removeInterestOp(SelectionKey key, int op) {
       int ops = key.readyOps();
       Logger logger = Logger.getLogger(getClass());
       logger.debug("Removing Ops for key '" + key + "' -- ");
       if ((ops & SelectionKey.OP_ACCEPT) != 0) {
           logger.debug("    ACCEPTING");
       }
       if ((ops & SelectionKey.OP_CONNECT) != 0) {
           logger.debug("    CONNECTING");
       }
       if ((ops & SelectionKey.OP_READ) != 0) {
           logger.debug("    READ");
       }
       if ((ops & SelectionKey.OP_WRITE) != 0) {
           logger.debug("    WRITE");
       }
       logger.debug("Op being removed -- ");
       if ((op & SelectionKey.OP_ACCEPT) != 0) {
           logger.debug("    ACCEPTING");
       }
       if ((op & SelectionKey.OP_CONNECT) != 0) {
           logger.debug("    CONNECTING");
       }
       if ((op & SelectionKey.OP_READ) != 0) {
           logger.debug("    READ");
       }
       if ((op & SelectionKey.OP_WRITE) != 0) {
           logger.debug("    WRITE");
       }
       if(!mySelector.keys().contains(key)) {
           /* TODO: Make this throw an exception */
           logger.debug("Unknown key.  Aborting.");
           /* Wake up the selector anyway, just in case */
           mySelector.wakeup();
           return;
       }
       if(addOps.containsKey(key)) {
           addOps.put(key, new Integer(((Integer)(addOps.get(key))).intValue() & ~op));
       } 
       if(removeOps.containsKey(key)) {
           removeOps.put(key, new Integer(((Integer)(removeOps.get(key))).intValue() | op));
       } else {
           removeOps.put(key, new Integer(ops));
       }
       
       logger.debug("Waking up selector");
       mySelector.wakeup();       
   }

   /**
     * Register a ChannelHandler with this IOThread
     */
    public SelectionKey register(ChannelHandler handler, int ops) 
        throws IOException {
        SelectableChannel chan = handler.getSelectableChannel();

        if (chan.isBlocking()) {
            throw new IllegalArgumentException("SelectableChannel is blocking");
        }

        SelectionKey key = chan.register(mySelector, ops, handler);
        handler.setSelectionKey(key);

        // XXX: Workaround, Selector hangs when it has nothing registered.
        synchronized (this) {
            notify();
        }
        return key;
    }
    
    public void deregister(ChannelHandler handler) {
        SelectionKey key=handler.getSelectableChannel().keyFor(mySelector);
        
        if(key!=null) {
            key.cancel();
        }
    }
    
    public void run() {
        Logger logger = Logger.getLogger(getClass());
        while (!isAborting()) {
            try {
                // XXX: Workaround, Selector.select() never returns if
                // there is nothing to select on ! (even if you 
                // call Selector.wakeup() )
                synchronized (this) {
                    try {
                        while (mySelector.keys().size() == 0) {
                            wait();
                        }
                    } catch (InterruptedException ie) {
                        logger.debug("Breaking out of IOThread");
                        break;
                    }
                }
                
                /* Look to apply ops to the keys first */
                logger.debug("Setting interest ops");
                Iterator it = addOps.keySet().iterator();
                while(it.hasNext()) {
                    SelectionKey next = (SelectionKey)it.next();
                    logger.debug("Setting an op");
                    int new_ops = ((Integer)addOps.get(next)).intValue();
                    next.interestOps(next.interestOps() | new_ops);
                    logger.debug("Done setting an op");
                    /* Remove the key from the map. */
                    addOps.remove(next);
                }

                logger.debug("Removing interest ops");
                it = removeOps.keySet().iterator();
                while(it.hasNext()) {
                    SelectionKey next = (SelectionKey)it.next();
                    logger.debug("Removing an op");
                    int new_ops = ((Integer)removeOps.get(next)).intValue();
                    next.interestOps(next.interestOps() & ~new_ops);
                    logger.debug("Done removing an op");
                    /* Remove the key from the map. */
                    removeOps.remove(next);
                }

                try {
                    boolean cont = true;
                    while(cont) {
                        logger.debug("Preparing to select");
                        int selected;
                        selected = mySelector.select();
                        cont = (selected == 0);
                        logger.debug("Done Selecting");
                    }
                } catch (InterruptedIOException iie) {
                    logger.debug("Breaking out of IOThread");
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
                            logger.debug("ACCEPTING CONNECTION");
                            handler.accept();
                        }
                        if ((ops & SelectionKey.OP_CONNECT) != 0) {
                            logger.debug("CONNECTING");
                            handler.connect();
                        }
                        if ((ops & SelectionKey.OP_READ) != 0) {
                            logger.debug("READING FROM CONNECTION");
                            handler.readFromChannel();
                            /* we always want to leave the reading as 
                             * an interested Op */
                            addInterestOp(key, SelectionKey.OP_READ);
                        }
                        if ((ops & SelectionKey.OP_WRITE) != 0) {
                            logger.debug("WRITING TO CONNECTION");
                            handler.writeToChannel();
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
                    deregister(handler);
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
