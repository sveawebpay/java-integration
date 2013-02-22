package se.sveaekonomi.webpay.integration.webservice.payment;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrderInformation;

public class InvoicePayment extends WebServicePayment {
     
    public InvoicePayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
        this.orderType = "Invoice";
    }
    
    public SveaCreateOrderInformation setOrderType(SveaCreateOrderInformation information) {
        this.orderInformation.AddressSelector = (!(this.createOrderBuilder.getAddressSelector() == null) ? this.createOrderBuilder.getAddressSelector() : "");
        this.orderInformation.OrderType = this.orderType;
        return this.orderInformation;
    }
}
