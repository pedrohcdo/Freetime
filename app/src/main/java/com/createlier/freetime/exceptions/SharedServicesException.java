package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class SharedServicesException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public SharedServicesException() {}

    /**
     * Constructor
     */
    public SharedServicesException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public SharedServicesException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public SharedServicesException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
