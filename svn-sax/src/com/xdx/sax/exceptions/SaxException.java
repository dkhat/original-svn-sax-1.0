package com.xdx.sax.exceptions;

/**
 * Generic superclass for SAX exceptions.
 */
public class SaxException
    extends RuntimeException {

    //
	private static final long serialVersionUID = 1L;

	public SaxException() {
    }

    public SaxException(String message) {
        super(message);
    }

    public SaxException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaxException(Throwable cause) {
        super(cause);
    }
}
