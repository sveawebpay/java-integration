package se.sveaekonomi.webpay.integration;

import java.util.List;
import java.util.Map;

import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddresses;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.GetPaymentPlanParams;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.PaymentPlanPricePerMonth;

/**
 * Start build request object by choosing the right method.
 * @author klar-sar
 */
public class WebPay {
    
    /**
     * Start build order request to create an order for all payments.
     * @param config
     * @return CreateOrderBuilder
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
