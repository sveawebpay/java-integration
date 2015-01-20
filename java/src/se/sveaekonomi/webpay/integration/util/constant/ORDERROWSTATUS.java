package se.sveaekonomi.webpay.integration.util.constant;

public enum ORDERROWSTATUS {	

	DELIVERED ("Delivered"),			
	NOTDELIVERED ("NotDelivered"),		
	CANCELLED ("Cancelled");			

	private final String constantAsString;
	
	ORDERROWSTATUS( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
	
	public String toString() {
		return this.constantAsString;
	}
	
	public static ORDERROWSTATUS fromString( String status ) throws Exception {
		for( ORDERROWSTATUS value : ORDERROWSTATUS.values() ) {
			if( value.toString().equals(status) ) return value;
		}
		throw new Exception("Unknown OrderRowStatus");
	}
}
