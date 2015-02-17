package se.sveaekonomi.webpay.integration.util.constant;

import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;

public enum PAYMENTTYPE {
    HOSTED,
    INVOICE,
    PAYMENTPLAN,
    HOSTED_ADMIN,
    ADMIN_TYPE;

	public static PAYMENTTYPE fromOrderType( ORDERTYPE orderType ) throws SveaWebPayException {
		if( orderType == ORDERTYPE.Invoice ) {
			return PAYMENTTYPE.INVOICE;
		}
		else if( orderType == ORDERTYPE.PaymentPlan ) {
			return PAYMENTTYPE.PAYMENTPLAN;
		}
		else {
			throw new SveaWebPayException("Unknown ordertype.");
		}
	}
}