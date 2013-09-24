package se.sveaekonomi.webpay.integration.hosted.payment;

import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

/**
 * Defines all direct payments to bank viewable in PayPage
 * @author klar-sar
 */
public class DirectPayment extends HostedPayment<DirectPayment> {
    
    public DirectPayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
    }
    
    protected DirectPayment configureExcludedPaymentMethods() {
        COUNTRYCODE countryCode = createOrderBuilder.getCountryCode();
        
        if (! countryCode.equals(COUNTRYCODE.SE)) {
            excludedPaymentMethods.add(PAYMENTMETHOD.SEB_SE.getValue());
            excludedPaymentMethods.add(PAYMENTMETHOD.NORDEA_SE.getValue());
            excludedPaymentMethods.add(PAYMENTMETHOD.SEBFTG_SE.getValue());
            excludedPaymentMethods.add(PAYMENTMETHOD.SHB_SE.getValue());
            excludedPaymentMethods.add(PAYMENTMETHOD.SWEDBANK_SE.getValue());
        }
        
        if (! countryCode.equals(COUNTRYCODE.NO)) {
            excludedPaymentMethods.add(PAYMENTMETHOD.BANKAXESS.getValue());
        }
        
        excludedPaymentMethods.add(PAYMENTMETHOD.PAYPAL.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.KORTCERT.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL.getValue());
        
        excludedPaymentMethods.addAll(excluded.excludeInvoicesAndPaymentPlan());
        return this;
    }
    
    public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) {
        return xmlw;
    }
}
