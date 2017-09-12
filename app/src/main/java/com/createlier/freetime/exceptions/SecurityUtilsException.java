package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class SecurityUtilsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public SecurityUtilsException() {}

    /**
     * Constructor
     */
    public SecurityUtilsException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public SecurityUtilsException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public SecurityUtilsException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
