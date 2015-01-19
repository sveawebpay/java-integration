package se.sveaekonomi.webpay.integration.util.constant;

public enum ORDERTYPE {
    Invoice("Invoice"),
    PaymentPlan("PaymentPlan");

	private final String constantAsString;
	
	ORDERTYPE( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
	
	public String toString() {
		return this.constantAsString;
	}
	
	public static ORDERTYPE fromString( String orderType ) throws Exception {
		for( ORDERTYPE value : ORDERTYPE.values() ) {
			if( value.toString().equals(orderType) ) return value;
		}
		throw new Exception("Unknown ordertype.");
	}
}
