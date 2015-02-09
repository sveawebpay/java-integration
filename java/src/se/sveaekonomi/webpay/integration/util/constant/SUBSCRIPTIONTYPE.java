package se.sveaekonomi.webpay.integration.util.constant;

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
	
	public static SUBSCRIPTIONTYPE fromString( String status ) throws Exception {
		for( SUBSCRIPTIONTYPE value : SUBSCRIPTIONTYPE.values() ) {
			if( value.toString().equals(status) ) return value;
		}
		throw new Exception("Unknown SubscriptionType");
	}
}
