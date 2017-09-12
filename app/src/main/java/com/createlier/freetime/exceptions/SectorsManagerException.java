package com.createlier.freetime.exceptions;

/**
 * Shared Service Exception
 *
 * @author Pedro Henrique
 * 
 */
public class SectorsManagerException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

    /**
     * Constructor
     */
    public SectorsManagerException() {}

    /**
     * Constructor
     */
    public SectorsManagerException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * Constructor
     */
    public SectorsManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor
     */
    public SectorsManagerException(Throwable cause) {
        super((cause == null ? null : cause.toString()), cause);
    }
	
}
