package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class SyncThreadPassTimeoutException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


    /**
     * Constructor
     */
    public SyncThreadPassTimeoutException() {}

    /**
     * Constructor
     */
    public SyncThreadPassTimeoutException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public SyncThreadPassTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public SyncThreadPassTimeoutException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
