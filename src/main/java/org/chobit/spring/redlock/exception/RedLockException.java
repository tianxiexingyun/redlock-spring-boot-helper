package org.chobit.spring.redlock.exception;


/**
 * @author rui.zhang
 */
public class RedLockException extends RuntimeException {

    public RedLockException() {
    }


    public RedLockException(String message) {
        super(message);
    }
}
