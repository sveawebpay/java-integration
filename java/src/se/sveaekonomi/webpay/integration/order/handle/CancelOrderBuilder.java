package se.sveaekonomi.webpay.integration.order.handle;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.AnnulTransactionRequest;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
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
public class CancelOrderBuilder {
    private final ConfigurationProvider config;
	private COUNTRYCODE countryCode;
	private Long orderId;

	public CancelOrderBuilder(ConfigurationProvider config) {
        this.config = config;
	}
	
    /**
     * Required
     * @param countryCode
     * @return CancelOrderBuilder
     */
	public CancelOrderBuilder setCountryCode(COUNTRYCODE countryCode) {
		this.countryCode = countryCode;
		return this;
	}

    /**
     * Required
     * @param orderId
     * @return CancelOrderBuilder
     */
	public CancelOrderBuilder setOrderId( Long orderId ) {
		this.orderId = orderId;
		return this;
	}
	
    /**
     * alias for setOrderId()
     * @param orderId
     * @return CancelOrderBuilder
     */
	public CancelOrderBuilder setTransactionId( Long transactionId ) {
		return this.setOrderId(transactionId);
	}	
	
	public CloseOrder cancelInvoiceOrder() {
		CloseOrderBuilder closeOrderBuilder = new CloseOrderBuilder(this.config);
		closeOrderBuilder.setCountryCode(this.countryCode);
		closeOrderBuilder.setOrderId(this.orderId);		
		closeOrderBuilder.setOrderType(ORDERTYPE.Invoice.toString());
		return new CloseOrder(closeOrderBuilder);
	}
	
	public CloseOrder cancelPaymentPlanOrder() {
		CloseOrderBuilder closeOrderBuilder = new CloseOrderBuilder(this.config);
		closeOrderBuilder.setCountryCode(this.countryCode);
		closeOrderBuilder.setOrderId(this.orderId);		
		closeOrderBuilder.setOrderType(ORDERTYPE.PaymentPlan.toString());
		return new CloseOrder(closeOrderBuilder);
	}
	
	public AnnulTransactionRequest cancelCardOrder() {
		AnnulTransactionRequest request = new AnnulTransactionRequest(this.config);
		request.setTransactionId(this.orderId.toString());
		request.setCountryCode(this.countryCode);
		return request;
	}	
}
