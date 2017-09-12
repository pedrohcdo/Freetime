package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class ProportionalLayoutException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public ProportionalLayoutException() {}

    /**
     * Constructor
     */
    public ProportionalLayoutException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public ProportionalLayoutException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor
     */
    public ProportionalLayoutException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}
