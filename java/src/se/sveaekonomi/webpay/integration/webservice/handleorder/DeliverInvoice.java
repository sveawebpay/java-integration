package se.sveaekonomi.webpay.integration.webservice.handleorder;

import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;

public class DeliverInvoice extends HandleOrder {

    public DeliverInvoice(DeliverOrderBuilder orderBuilder) {
        super(orderBuilder);
    }
}
