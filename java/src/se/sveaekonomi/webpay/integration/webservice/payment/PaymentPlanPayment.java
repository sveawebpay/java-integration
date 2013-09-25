package se.sveaekonomi.webpay.integration.webservice.payment;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrderInformation;

public class PaymentPlanPayment extends WebServicePayment {
    
    public PaymentPlanPayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
        this.orderType = PAYMENTTYPE.PAYMENTPLAN;
    }
    
    public SveaCreateOrderInformation setOrderType() {
        if (this.createOrderBuilder.getIsCompanyIdentity() && this.createOrderBuilder.getCompanyCustomer().getAddressSelector() != null) {
            this.orderInformation.AddressSelector = this.createOrderBuilder.getCompanyCustomer().getAddressSelector();
        } else {
            this.orderInformation.AddressSelector = "";
        }
        
        this.orderInformation.OrderType = "PaymentPlan";
        
        return this.orderInformation;
    }
}
