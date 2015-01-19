package se.sveaekonomi.webpay.integration.util.constant;

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
	
	public static DISTRIBUTIONTYPE fromString( String orderType ) throws Exception {
		for( DISTRIBUTIONTYPE value : DISTRIBUTIONTYPE.values() ) {
			if( value.toString().equals(orderType) ) return value;
		}
		throw new Exception("Unknown ordertype.");
	}
}
