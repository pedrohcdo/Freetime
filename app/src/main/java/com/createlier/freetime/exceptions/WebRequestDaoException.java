package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class WebRequestDaoException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public WebRequestDaoException() {}

    /**
     * Constructor
     */
    public WebRequestDaoException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public WebRequestDaoException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructor
     */
    public WebRequestDaoException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
}
