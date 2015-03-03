package se.sveaekonomi.webpay.integration.util.constant;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public enum PAYMENTTYPE {
    HOSTED("HOSTED"),
    INVOICE("Invoice"),
    PAYMENTPLAN("PaymentPlan"),
    HOSTED_ADMIN("HOSTED_ADMIN"),
    ADMIN_TYPE("ADMIN_TYPE");

	private final String constantAsString;
    
	PAYMENTTYPE( String constantAsString ) {
		this.constantAsString = constantAsString;
	}
    
	public String toString() {
		return this.constantAsString;
	}
	
	public static PAYMENTTYPE fromString( String orderType ) throws SveaWebPayException {
		for( PAYMENTTYPE value : PAYMENTTYPE.values() ) {
			if( value.toString().equals(orderType) ) return value;
		}
		throw new SveaWebPayException("Unknown payment type.");
	}	
	
	
//	public static PAYMENTTYPE fromOrderType( ORDERTYPE orderType ) throws SveaWebPayException {
//		if( orderType == ORDERTYPE.Invoice ) {
//			return PAYMENTTYPE.INVOICE;
//		}
//		else if( orderType == ORDERTYPE.PaymentPlan ) {
//			return PAYMENTTYPE.PAYMENTPLAN;
//		}
//		else {
//			throw new SveaWebPayException("Unknown ordertype.");
//		}
//	}
}