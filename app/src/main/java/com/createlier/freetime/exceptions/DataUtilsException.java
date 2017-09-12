package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class DataUtilsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public DataUtilsException() {}

    /**
     * Constructor
     */
    public DataUtilsException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public DataUtilsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor
     */
    public DataUtilsException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}
