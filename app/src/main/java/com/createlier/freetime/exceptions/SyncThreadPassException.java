package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class SyncThreadPassException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public SyncThreadPassException() {}

    /**
     * Constructor
     */
    public SyncThreadPassException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public SyncThreadPassException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public SyncThreadPassException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
