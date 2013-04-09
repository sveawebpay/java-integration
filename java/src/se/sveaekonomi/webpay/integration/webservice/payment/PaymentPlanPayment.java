package se.sveaekonomi.webpay.integration.webservice.payment;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrderInformation;

public class PaymentPlanPayment extends WebServicePayment {
    
    public PaymentPlanPayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
        this.orderType = PAYMENTTYPE.PAYMENTPLAN;
    }
    
    public SveaCreateOrderInformation setOrderType(SveaCreateOrderInformation information) {
        this.orderInformation.AddressSelector = (!(this.createOrderBuilder.getAddressSelector() == null) ? this.createOrderBuilder.getAddressSelector() : "");
        this.orderInformation.OrderType = "PaymentPlan";
        return this.orderInformation;
    }
}
