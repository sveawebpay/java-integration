package se.sveaekonomi.webpay.integration.exception;

public class SveaWebPayException extends RuntimeException {
	
	public SveaWebPayException(String message, Throwable t) {
		super(message, t);
	}

	private static final long serialVersionUID = -1974684452302746986L;
}
