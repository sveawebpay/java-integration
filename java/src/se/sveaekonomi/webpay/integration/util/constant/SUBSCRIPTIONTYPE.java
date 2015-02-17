package se.sveaekonomi.webpay.integration.util.constant;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public enum SUBSCRIPTIONTYPE {	

	RECURRING ("RECURRING"),
	RECURRINGCAPTURE ("RECURRINGCAPTURE"),
	ONECLICK ("ONECLICK"),
	ONECLICKCAPTURE ("ONECLICKCAPTURE");

	private final String constantAsString;
	
	SUBSCRIPTIONTYPE( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
	
	public String toString() {
		return this.constantAsString;
	}
	
	public static SUBSCRIPTIONTYPE fromString( String status ) throws SveaWebPayException {
		for( SUBSCRIPTIONTYPE value : SUBSCRIPTIONTYPE.values() ) {
			if( value.toString().equals(status) ) return value;
		}
		throw new SveaWebPayException("Unknown subscription type");
	}
}
