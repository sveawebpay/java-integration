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
}
