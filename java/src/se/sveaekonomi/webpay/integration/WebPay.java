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
      //  return new CreateOrderBuilder();
    }     
    
    public static CloseOrderBuilder closeOrder(Config config) {
        return new CloseOrderBuilder(config);
    }
    
    public static CloseOrderBuilder closeOrder() {
        return closeOrder(SveaConfig.createTestConfig());
     	// return new CloseOrderBuilder();
    }
    
    public static DeliverOrderBuilder deliverOrder(Config config) {
        return new DeliverOrderBuilder(config);
    }
    
    public static DeliverOrderBuilder deliverOrder() {
    	return deliverOrder(SveaConfig.createTestConfig());
    	//   return new DeliverOrderBuilder();
    }
    
    public static GetPaymentPlanParams getPaymentPlanParams() {
        return new GetPaymentPlanParams();
    }
    
    public static GetAddresses getAddresses() {
        return new GetAddresses();
    }
}
