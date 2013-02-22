package se.sveaekonomi.webpay.integration.hosted.payment;

import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;


public class PaymentMethodPayment extends HostedPayment {
    
    private String paymentMethod;
    
    public PaymentMethodPayment(CreateOrderBuilder createOrderBuilder, String paymentMethod) {
        super(createOrderBuilder);
        this.paymentMethod = paymentMethod;
    }

    /**
     * Only used in CardPayment and DirectPayment
     */
    @Override
    protected HostedPayment configureExcludedPaymentMethods() {
        return this;
    }

    @Override
    public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) throws Exception {
        if (paymentMethod != null) {
            writeSimpleElement(xmlw, "paymentmethod", paymentMethod.toString());
        }
        
        return xmlw;
    }
}
