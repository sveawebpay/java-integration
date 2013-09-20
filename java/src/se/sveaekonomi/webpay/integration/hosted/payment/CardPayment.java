package se.sveaekonomi.webpay.integration.hosted.payment;

import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

/**
 * Defines all card payments viewable in PayPage
 * @author klar-sar
 */
public class CardPayment extends HostedPayment<CardPayment> {
    
    public CardPayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
    }
    
    protected CardPayment configureExcludedPaymentMethods() {
        //Payment service providers
        excludedPaymentMethods.add(PAYMENTMETHOD.PAYPAL.getValue());
        
        //Direct bank payment methods
        excludedPaymentMethods.add(PAYMENTMETHOD.NORDEA_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SEB_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SEBFTG_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SHB_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SWEDBANK_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.BANKAXESS.getValue());  
        
        //Invoices and payment plan      
        excludedPaymentMethods.addAll(excluded.excludeInvoicesAndPaymentPlan());
        
        return this;
    }
    
    public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) {
        return xmlw;
    }
}
