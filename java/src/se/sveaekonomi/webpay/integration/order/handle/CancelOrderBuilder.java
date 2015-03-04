package se.sveaekonomi.webpay.integration.order.handle;

import javax.xml.bind.ValidationException;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.AnnulTransactionRequest;
import se.sveaekonomi.webpay.integration.order.OrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;

/**
 * CancelOrderBuilder is the class used to cancel an order with Svea, that has
 * not yet been delivered (invoice, payment plan) or been confirmed (card).
 * 
 * Supports Invoice, Payment Plan and Card orders. For Direct Bank orders, @see
 * CreditOrderBuilder instead.
 * 
 * Use setOrderId() to specify the Svea order id, this is the order id returned 
 * with the original create order request response.
 *
 * Use setCountryCode() to specify the country code matching the original create
 * order request.
 * 
 * Use either cancelInvoiceOrder(), cancelPaymentPlanOrder or cancelCardOrder,
 * which ever matches the payment method used in the original order request.
 *  
 * The final doRequest() will send the cancelOrder request to Svea, and the 
 * resulting response object contents holds outcome of the request. 
 *     
 *    $response = WebPay::cancelOrder($config)
 *        ->setCountryCode("SE")          // Required. Use same country code as in createOrder request.
 *        ->setOrderId($orderId)          // Required. Use SveaOrderId received with createOrder response
 *        ->cancelInvoiceOrder()          // Use the method corresponding to the original createOrder payment method.
 *        //->cancelPaymentPlanOrder()     
 *        //->cancelCardOrder()           
 *             ->doRequest()
 *    ; 
 * 
 * @author Kristian Grossman-Madsen for Svea WebPay
 */
public class CancelOrderBuilder extends OrderBuilder<CancelOrderBuilder>{
	private Long orderId;


//    /**
//     * Required
//     * @param countryCode
//     * @return CancelOrderBuilder
//     */
//	public CancelOrderBuilder setCountryCode(COUNTRYCODE countryCode) {
//		this.countryCode = countryCode;
//		return this;
//	}

    /**
     * Required
     * @param orderId
     * @return CancelOrderBuilder
     */
	public CancelOrderBuilder setOrderId( Long orderId ) {
		this.orderId = orderId;
		return this;
	}
	
	public Long getOrderId() {
		return this.orderId;
	}	
	
	/**
	 * optional, card only -- alias for setOrderId
     * @param orderId
     * @return CancelOrderBuilder
     */
	public CancelOrderBuilder setTransactionId( Long transactionId ) {
		return this.setOrderId(transactionId);
	}
	
	/**
	 * optional, card only -- alias for setOrderId
     * @deprecated
	 * @param transactionId as string, i.e. as transactionId is returned in HostedPaymentResponse
	 * @return DeliverOrderRowsBuilder
	 */
    public CancelOrderBuilder setTransactionId( String transactionId) {        
        return this.setOrderId( Long.parseLong(transactionId) );
    } 	
	
	public CloseOrder cancelInvoiceOrder() {
	    // TODO remove dependency on deprecated CloseOrder
		CloseOrderBuilder closeOrderBuilder = new CloseOrderBuilder(this.config);
		closeOrderBuilder.setCountryCode(this.countryCode);
		closeOrderBuilder.setOrderId(this.orderId);		
		closeOrderBuilder.setOrderType(ORDERTYPE.Invoice.toString());
		return new CloseOrder(closeOrderBuilder);
	}
	
	public CloseOrder cancelPaymentPlanOrder() {
	    // TODO remove dependency on deprecated CloseOrder
		CloseOrderBuilder closeOrderBuilder = new CloseOrderBuilder(this.config);
		closeOrderBuilder.setCountryCode(this.countryCode);
		closeOrderBuilder.setOrderId(this.orderId);		
		closeOrderBuilder.setOrderType(ORDERTYPE.PaymentPlan.toString());
		return new CloseOrder(closeOrderBuilder);
	}
	
	
	public CancelOrderBuilder(ConfigurationProvider config) {
        this.config = config;
	}
	
	public AnnulTransactionRequest cancelCardOrder() {
		
    	// validate request and throw exception if validation fails
        String errors = validateCancelCardOrder();
        
        if (!errors.equals("")) {
            throw new SveaWebPayException("Validation failed", new ValidationException(errors));
        } 
				
		AnnulTransactionRequest request = new AnnulTransactionRequest(this.getConfig());
		request.setCountryCode(this.getCountryCode());
		request.setTransactionId(Long.toString(this.orderId));		
		return request;
	}
	
	// validates CancelCardOrder required attributes
    public String validateCancelCardOrder() {
        String errors = "";
        if (this.getCountryCode() == null) {
            errors += "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";
        }
        
        if (this.getOrderId() == null) {
            errors += "MISSING VALUE - OrderId is required, use setOrderId().\n";
    	}
        return errors;    
    }
}
