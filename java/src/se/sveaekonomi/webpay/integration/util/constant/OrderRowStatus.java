package se.sveaekonomi.webpay.integration.util.constant;

public enum OrderRowStatus {	

	DELIVERED ("Delivered"),			
	NOTDELIVERED ("NotDelivered"),		
	CANCELLED ("Cancelled");			

	private final String constantAsString;
	
	OrderRowStatus( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
	
	public String toString() {
		return this.constantAsString;
	}
	
	public static OrderRowStatus fromString( String status ) throws Exception {
		for( OrderRowStatus value : OrderRowStatus.values() ) {
			if( value.toString().equals(status) ) return value;
		}
		throw new Exception("Unknown OrderRowStatus");
	}
}
