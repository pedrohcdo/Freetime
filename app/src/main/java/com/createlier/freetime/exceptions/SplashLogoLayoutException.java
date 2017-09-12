package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class SplashLogoLayoutException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public SplashLogoLayoutException() {}

    /**
     * Constructor
     */
    public SplashLogoLayoutException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public SplashLogoLayoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public SplashLogoLayoutException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
