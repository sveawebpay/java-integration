package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.Requestable;
import se.sveaekonomi.webpay.integration.Respondable;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodPayment;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.validator.HostedOrderValidator;
import se.sveaekonomi.webpay.integration.order.validator.IdentityValidator;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

public class ConfirmTransactionRequest implements Requestable {

	private DeliverOrderBuilder builder;
	
	public ConfirmTransactionRequest( DeliverOrderBuilder builder ) {
		this.builder = builder;				
	}
	
	@Override
	public ConfirmTransactionResponse doRequest() {
		// TODO stub
		return new ConfirmTransactionResponse();
	}

	@Override
	public <T> T prepareRequest() {
		
        String errors = validateOrder();        
        if (errors.length() > 0) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        }
				
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * validates that all required attributes needed for the request are present in the builder object
	 * @return indicating which methods are missing, or empty String if no problems found
	 */
	public String validateOrder() {
		String errors = "";		
		errors += validateOrderId(this.builder);
		errors += validateCountryCode(this.builder);
		errors += validateCaptureDate(this.builder);
		return errors;
	}
	
    private String validateOrderId(DeliverOrderBuilder order) {
    	return (order.getOrderId() == 0L) ? "MISSING VALUE - setOrderId is required.\n" : "";	// orderId is long, i.e. initialised as 0
    }
   
    private String validateCountryCode(DeliverOrderBuilder order) {
        return (order.getCountryCode() == null) ? "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" : "";
    }
    
    private String validateCaptureDate(DeliverOrderBuilder order) {
        return (order.getCaptureDate() == null) ? "MISSING VALUE - setOrderId is required.\n" : "";
    }
}
