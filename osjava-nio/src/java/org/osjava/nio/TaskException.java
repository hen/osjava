package org.osjava.nio;

/**
 * A class which wraps an exception which was thrown by a Task executed
 * in another thread.
 */
public class TaskException extends RuntimeException {

    /**
     * Create a new TaskException to wrap the specified Exception
     *
     * @param wrapped the Exception to wrap.
     */
    public TaskException(Exception wrapped) {
        super(wrapped);
    }

}
