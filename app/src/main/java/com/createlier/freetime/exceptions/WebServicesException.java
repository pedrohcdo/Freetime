package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class WebServicesException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public WebServicesException() {}

    /**
     * Constructor
     */
    public WebServicesException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public WebServicesException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public WebServicesException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
