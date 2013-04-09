package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.config.Config;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddresses;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.GetPaymentPlanParams;

/**
 * Start build request object by choosing the right method.
 * @author klar-sar
 *
 */
public class WebPay {
    
	/**
	 * Start build order request to create an order for all payments.
	 * @param config
	 * @return CreateOrderBuilder
	 */
    public static CreateOrderBuilder createOrder(ConfigurationProvider config) {
        return new CreateOrderBuilder(config);
    }
    
    /**
     * Start build order request to create an order for all payments.
     * @return CreateOrderBuilder
     */
    public static CreateOrderBuilder createOrder() {    	
    	return createOrder(SveaConfig.getDefaultConfig()); 
    }     
    
    /**
     * Start building request to close order.
     * @param config
     * @return CloseOrderBuilder
     */
    public static CloseOrderBuilder closeOrder(ConfigurationProvider config) {
        return new CloseOrderBuilder(config);
    }
    
    /**
     * Start building request to close order.
     * @return CloseOrderBuilder
     */
    public static CloseOrderBuilder closeOrder() {
        return closeOrder(SveaConfig.getDefaultConfig());
    }
    
    /**
     * Starts building request for deliver order.
     * @return DeliverOrderBuilder
     */
    public static DeliverOrderBuilder deliverOrder(Config config) {
        return new DeliverOrderBuilder(config);
    }
    
    /**
     * Starts building request for deliver order.
     * @return DeliverOrderBuilder
     */
    public static DeliverOrderBuilder deliverOrder() {
    	return deliverOrder(SveaConfig.createTestConfig());
    }
    
    /**
     * Get payment plan parameters to present to customer before creating a payment plan payment request
     * @param config
     * @return GetPaymentPlanParams
     */
    public static GetPaymentPlanParams getPaymentPlanParams(Config config) {
        return new GetPaymentPlanParams(config);
    }
    
    /**
     * Get payment plan parameters to present to customer before creating a payment plan payment request
     * @return GetPaymentPlanParams
     */
    public static GetPaymentPlanParams getPaymentPlanParams() {
    	return getPaymentPlanParams(SveaConfig.createTestConfig());
    }
    
    /**
     * Start building request for getting addresses.
     * @param config
     * @return GetAddresses
     */
    public static GetAddresses getAddresses(Config config) {
        return new GetAddresses(config);
    }
    
    /**
     * Start building request for getting addresses.
     * @return GetAddresses
     */
    public static GetAddresses getAddresses() {
        return getAddresses(SveaConfig.createTestConfig());
    }
}
