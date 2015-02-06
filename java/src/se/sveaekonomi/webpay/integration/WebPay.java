package se.sveaekonomi.webpay.integration;

import java.util.List;
import java.util.Map;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.payment.PayPagePayment;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddresses;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.GetPaymentPlanParams;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.PaymentPlanPricePerMonth;

/**
 * Start build request object by choosing the right method.
 * @author klar-sar, Kristian Grossman-Madsen
 */
public class WebPay {
    
    /**
     * Use WebPay.createOrder() to create an order using invoice, payment plan, card, or direct bank payment methods. 
     * You may also send the customer to the PayPage, where they may select from all available payment methods.
     *
     * See the CreateOrderBuilder class for more info on methods used to specify the order builder contents, including
     * order rows items et al, and then specifying which payment method to use, followed by sending the request to Svea
     * using doRequest, and parsing the response received from Svea.
     *
     * Invoice and payment plan orders will perform a synchronous payment request, and will return a response 
     * object immediately following the doRequest call.
     * 
     * Hosted payment methods like Card, Direct Bank and any payment methods accessed via the PayPage, are asynchronous.
     * Having selecting an asynchronous payment method you generally use a request class method to get a payment form 
     * object in return. The form is then posted to Svea, where the customer is redirected to the card payment provider 
     * service or bank. After the customer completes the payment, a response is sent back to your provided return url, 
     * where it can be processed and inspected.
     * 
     * Card, Direct Bank, and other hosted methods accessed via PayPage are asynchronous. Asynchronous payment methods
     * provide an html form containing a formatted message to send to Svea, which in turn will send a request response 
     * to the specified return url, where the response can be parsed using the SveaResponse class. You should also be
     * prepared to receive the request response on the specified alternative callback url which is used, amongst others,
     * if i.e. the customer does not return to the store after the order payment have been completed.
     * 
     * To create an invoice or partpayment order using useInvoicePayment or usePaymentPlanPayment, you do not need to 
     * explicitly specify which payment methods are available. 
     * 
     * When creating a card or direct bank order, you can minimize the number of steps in the checkout process by 
     * explicitly specifying i.e. usePaymentMethod(PAYMENTMETHOD.KORTCERT) instead of going through useCardPayment.
     * 
     * Get an order builder instance using the WebPay.deliverOrder entrypoint, then provide more information about the 
     * transaction using DeliverOrderBuilder methods: 
     * 
     * When redirecting the customer to the PayPage, you can use methods in PayPagePayment, i.e. excludePaymentMethods, 
     * to first specify which available payment methods to show or exclude, followed by the doRequest call.
     * 
     * ...
     *      orderbuilder = WebPay.createOrder(config)
     *       	.addOrderRow()              		// required, see WebPayItem.orderRow() for order row specification
     *          .addFee()         			   		// optional, see WebPayItem for invoice, shipping fee
     *          .addDiscount()          			// optional, see WebPayItem for fixed, relative discount
     *          .addCustomerDetails()    			// required for invoice and payment plan payments, see WebPayItem for individual, company customer
     *          .setCountryCode()               	// required
     *          .setOrderDate()            			// required for invoice and payment plan payments
     *          .setCurrency()                 		// required for card payment, direct bank & PayPage payments only. Ignored for invoice and payment plan.
     *          .setClientOrderNumber()    			// required for card payment, direct payment, PaymentMethod & PayPage payments, max length 30 chars.
     *          .setCustomerReference()    			// optional, ignored for card & direct bank orders, max length 30 chars.
     *      ;
     *      
     *      // then select a synchronous payment method (invoice, part payment) request class and send request
     *      response = orderbuilder.useInvoicePayment().doRequest();    	// returns CreateOrderResponse
     *      response = orderbuilder.usePaymentPlanPayment().doRequest();	// returns CreateOrderResponse
     *      
     *      // or select an asynchronous payment method (card, direct bank et al.) request class
     *      request = orderbuilder
     *      	.usePaymentMethod(PAYMENTMETHOD.KORTCERT)	// returns HostedPayment<?>
	 *			.usePayPage()								// returns PayPagePayment
     *      	.usePayPageCardOnly()						// returns CardPayment
     *      	.usePayPageDirectBankOnly()					// returns DirectPayment
     *      ;
     *      // then perform any additional request settings needed and receive request information
     *      request.
	 *			.setReturnUrl()				// required
     *      	.setCallbackUrl()			// optional but recommended
     *      	.setCancelUrl()				// optional, paypage only
     *      	.setPayPageLanguageCode()	// optional, defaults to english
     *      	.setSubscriptionType()		// optional, card only, used to setup recurring payments
     *      	.setSubscriptionId()		// required for card doRecur request
     *      ;
     *      form = request.getPaymentForm();	// returns PaymentForm object containing request html form
     *      url = request.getPaymentUrl();		// returns PaymentUrl object containing url to prepared payment request
     *      response = request.doRecur();		// performs synchronous request, returns RecurTransactionResponse
     * ...
     * 
     */
	public static CreateOrderBuilder createOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new CreateOrderBuilder(config);
    }  

    
    /**
     * Use the WebPay.deliverOrder() entrypoint when you deliver an order to the customer. 
     * Supports Invoice, Payment Plan and Card orders. (Direct Bank orders are not supported.)
     * 
     * The deliver order request should generally be sent to Svea once the ordered 
     * items have been sent out, or otherwise delivered, to the customer. 
     * 
     * For invoice and partpayment orders, the deliver order request triggers the 
     * invoice being sent out to the customer by Svea. (This assumes that your account
     * has auto-approval of invoices turned on, please contact Svea if unsure). 
     * 
     * For card orders, the deliver order request confirms the card transaction, 
     * which in turn allows nightly batch processing of the transaction by Svea.  
     * (Delivering card orders is only needed if your account has auto-confirm
     * turned off, please contact Svea if unsure.)
     * 
     * To deliver an invoice, partpayment or card order in full, you do not need to 
     * specify order rows. To partially deliver an order, the recommended way is to
     * use WebPayAdmin.deliverOrderRows().
     *  
     * Get an order builder instance using the WebPay.deliverOrder entrypoint, then
     * provide more information about the transaction using DeliverOrderBuilder methods: 
     * 
     * ...
     * 		request = WebPay.deliverOrder(config)
     *          .setOrderId()                  // invoice or payment plan only, required
     *          .setTransactionId()            // card only, optional, alias for setOrderId 
     *          .setCountryCode()              // required
     *          .setInvoiceDistributionType()  // invoice only, required
     *          .setNumberOfCreditDays()       // invoice only, optional
     *          .setCaptureDate()              // card only, optional
     *          .addOrderRow()                 // deprecated, optional -- use WebPayAdmin.deliverOrderRows instead
     *          .setCreditInvoice()            // deprecated, optional -- use WebPayAdmin.creditOrderRows instead
     *      ;
     *      // then select the corresponding request class and send request
     *      response = request.deliverInvoiceOrder().doRequest();       // returns DeliverOrderResponse
     *      response = request.deliverPaymentPlanOrder().doRequest();   // returns DeliverOrderResponse
     *      response = request.deliverCardOrder().doRequest();          // returns ConfirmTransactionResponse
     * ...
     * 
     * @return DeliverOrderBuilder
     */
    public static DeliverOrderBuilder deliverOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new DeliverOrderBuilder(config);
    }
    

    /**
     * Get payment plan parameters to present to customer before creating a payment plan payment request
     * @param config
     * @return GetPaymentPlanParams
     */
    public static GetPaymentPlanParams getPaymentPlanParams(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new GetPaymentPlanParams(config);
    }
    

    /**
     * Start building request for getting addresses.
     * @param config
     * @return GetAddresses
     */
    public static GetAddresses getAddresses(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new GetAddresses(config);
    }
    
    
    /**
     * Calculate the prices per month for the payment plan campaigns
     * @param type Double amount
     * @param type PaymentPlanParamsResponse params
     * @return List<Map<String, String>>
     */
    public static List<Map<String, String>> paymentPlanPricePerMonth(Double amount, PaymentPlanParamsResponse params) {
        return new PaymentPlanPricePerMonth().calculate(amount, params);
    }


    
    // deprecated below    
    /**
     * Start building request to close order.
     * @param config
     * @return CloseOrderBuilder
     * @deprecated Since 1.6.0 -- use WebPayAdmin.cancelOrder() instead 
     */
    public static CloseOrderBuilder closeOrder(ConfigurationProvider config) {
        if (config == null) {
            throw new SveaWebPayException("A configuration must be provided. For testing purposes use SveaConfig.GetDefaultConfig()");
        }
    	
        return new CloseOrderBuilder(config);
    }
      
    /**
     * Start build order request to create an order for all payments.
     * @return CreateOrderBuilder
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static CreateOrderBuilder createOrder() {
        return createOrder(null); 
    }
    /**
     * Start building request to close order.
     * @return CloseOrderBuilder
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static CloseOrderBuilder closeOrder() {
        return closeOrder(null);
    }
    /**
     * Starts building request for deliver order.
     * @return DeliverOrderBuilder
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static DeliverOrderBuilder deliverOrder() {
        return deliverOrder(null);
    }
    /**
     * Get payment plan parameters to present to customer before creating a payment plan payment request
     * @return GetPaymentPlanParams
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static GetPaymentPlanParams getPaymentPlanParams() {
        return getPaymentPlanParams(null);
    }
    /**
     * Start building request for getting addresses.
     * @return GetAddresses
     * 
     * @deprecated A configuration must be provided. For testing purposes use {@link SveaConfig.GetDefaultConfig()}.
     */
    @Deprecated
    public static GetAddresses getAddresses() {
        return getAddresses(null);
    }
}
