package se.sveaekonomi.webpay.integration.util.constant;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public enum DISTRIBUTIONTYPE {
    Post("Post"),
    Email("Email");

	private final String constantAsString;
	
	DISTRIBUTIONTYPE( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
	
	public String toString() {
		return this.constantAsString;
	}
	
	public static DISTRIBUTIONTYPE fromString( String distributionType ) throws SveaWebPayException {
		for( DISTRIBUTIONTYPE value : DISTRIBUTIONTYPE.values() ) {
			if( value.toString().equals(distributionType) ) return value;
		}
		throw new SveaWebPayException("Unknown DistributionType: "+distributionType);
	}
}
