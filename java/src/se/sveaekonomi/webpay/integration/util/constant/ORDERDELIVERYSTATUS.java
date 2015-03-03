package se.sveaekonomi.webpay.integration.util.constant;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public enum ORDERDELIVERYSTATUS {	

	CREATED("Created"),
	PARTIALLYDELIVERED ("PartiallyDelivered"),		
	DELIVERED ("Delivered"),			
	CANCELLED ("Cancelled");			

	private final String constantAsString;
	
	ORDERDELIVERYSTATUS( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
	
	public String toString() {
		return this.constantAsString;
	}
	
	public static ORDERDELIVERYSTATUS fromString( String status ) throws SveaWebPayException {
		for( ORDERDELIVERYSTATUS value : ORDERDELIVERYSTATUS.values() ) {
			if( value.toString().equals(status) ) return value;
		}
		throw new SveaWebPayException("Unknown OrderDeliveryStatus: "+status);
	}
}
