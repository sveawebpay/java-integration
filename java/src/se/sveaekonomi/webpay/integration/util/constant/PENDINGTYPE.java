package se.sveaekonomi.webpay.integration.util.constant;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public enum PENDINGTYPE {	

	SMSONHIGHAMOUNT ("SMSOnHighAmount"),
	USEOFDELIVERYADDRESS ("UseOfDeliveryAddress");

	private final String constantAsString;
	
	PENDINGTYPE( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
	
	public String toString() {
		return this.constantAsString;
	}
	
	public static PENDINGTYPE fromString( String type ) throws SveaWebPayException {
		for( PENDINGTYPE value : PENDINGTYPE.values() ) {
			if( value.toString().equals(type) ) return value;
		}
		throw new SveaWebPayException("Unknown PendingType: "+type);
	}
}
