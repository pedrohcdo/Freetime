package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class LocalDatabaseException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public LocalDatabaseException() {}

    /**
     * Constructor
     */
    public LocalDatabaseException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public LocalDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor
     */
    public LocalDatabaseException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}
