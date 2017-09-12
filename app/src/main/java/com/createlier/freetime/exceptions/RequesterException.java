package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class RequesterException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public RequesterException() {}

    /**
     * Constructor
     */
    public RequesterException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public RequesterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public RequesterException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
