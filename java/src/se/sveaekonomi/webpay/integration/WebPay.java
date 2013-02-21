package se.sveaekonomi.webpay.integration;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddresses;
import se.sveaekonomi.webpay.integration.webservice.getpaymentplanparams.GetPaymentPlanParams;

public class WebPay {
    
    public static CreateOrderBuilder createOrder() {
        return new CreateOrderBuilder();
    }
    
    public static CloseOrderBuilder closeOrder() {
        return new CloseOrderBuilder();
    }
    
    public static DeliverOrderBuilder deliverOrder() {
        return new DeliverOrderBuilder();
    }
    
    public static GetPaymentPlanParams getPaymentPlanParams() {
        return new GetPaymentPlanParams();
    }
    
    public static GetAddresses getAddresses() {
        return new GetAddresses();
    }
}
