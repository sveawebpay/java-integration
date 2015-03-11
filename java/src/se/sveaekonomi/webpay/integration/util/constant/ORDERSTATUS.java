package se.sveaekonomi.webpay.integration.util.constant;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public enum ORDERSTATUS {	

	CREATED ("Created"),
	PENDING ("Pending"),
	ACTIVE ("Active"),
	DENIED ("Denied"),
	ERROR ("Error");

	private final String constantAsString;
	
	ORDERSTATUS( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
	
	public String toString() {
		return this.constantAsString;
	}
	
	public static ORDERSTATUS fromString( String status ) throws SveaWebPayException {
		for( ORDERSTATUS value : ORDERSTATUS.values() ) {
			if( value.toString().equals(status) ) return value;
		}
		throw new SveaWebPayException("Unknown OrderStatus: "+status);
	}
}
