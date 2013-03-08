package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.config.Config;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddresses;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.GetPaymentPlanParams;

public class WebPay {
    
    public static CreateOrderBuilder createOrder(Config config) {
        return new CreateOrderBuilder(config);
    }
    
    public static CreateOrderBuilder createOrder() {
    	return createOrder(SveaConfig.createTestConfig()); 
    }     
    
    public static CloseOrderBuilder closeOrder(Config config) {
        return new CloseOrderBuilder(config);
    }
    
    public static CloseOrderBuilder closeOrder() {
        return closeOrder(SveaConfig.createTestConfig());
    }
    
    public static DeliverOrderBuilder deliverOrder(Config config) {
        return new DeliverOrderBuilder(config);
    }
    
    public static DeliverOrderBuilder deliverOrder() {
    	return deliverOrder(SveaConfig.createTestConfig());
    }
    
    public static GetPaymentPlanParams getPaymentPlanParams(Config config) {
        return new GetPaymentPlanParams(config);
    }
    
    public static GetPaymentPlanParams getPaymentPlanParams() {
    	return getPaymentPlanParams(SveaConfig.createTestConfig());
    }
    
    public static GetAddresses getAddresses(Config config) {
        return new GetAddresses(config);
    }
    
    public static GetAddresses getAddresses() {
        return getAddresses(SveaConfig.createTestConfig());
    }
}
