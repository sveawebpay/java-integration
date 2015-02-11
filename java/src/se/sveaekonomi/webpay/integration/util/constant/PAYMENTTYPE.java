package se.sveaekonomi.webpay.integration.util.constant;

public enum PAYMENTTYPE {
    HOSTED,
    INVOICE,
    PAYMENTPLAN,
    HOSTED_ADMIN,
    ADMIN_TYPE;

	public static PAYMENTTYPE fromOrderType( ORDERTYPE orderType ) {
		if( orderType == ORDERTYPE.Invoice ) {
			return PAYMENTTYPE.INVOICE;
		}
		else if( orderType == ORDERTYPE.PaymentPlan ) {
			return PAYMENTTYPE.PAYMENTPLAN;
		}
		else {
			throw new RuntimeException("Unknown ordertype.");	// TODO not very nice, but shouldn't happen...
		}
	}
}