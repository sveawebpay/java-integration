package se.sveaekonomi.webpay.integration.hosted.payment;

import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

public class CardPayment extends HostedPayment {
    
    public CardPayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
    }
    
    protected HostedPayment configureExcludedPaymentMethods() {      
        
        COUNTRYCODE countryCode = createOrderBuilder.getCountryCode();
        
        if (countryCode.equals(COUNTRYCODE.SE)) {
            excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL.getValue());
        }
        
        //Payment service providers
        excludedPaymentMethods.add(PAYMENTMETHOD.PAYPAL.getValue());
        
        //Direct bank payment methods
        excludedPaymentMethods.add(PAYMENTMETHOD.NORDEA_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SEB_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SEBFTG_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SHB_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SWEDBANK_SE.getValue());                           
        
        //Invoices and payment plan      
        excludedPaymentMethods.addAll(excluded.excludeInvoicesAndPaymentPlan());        
        
        return this;
    }
    
    public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) {
        return xmlw;
    }
}
