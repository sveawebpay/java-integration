package se.sveaekonomi.webpay.integration.hosted.payment;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.INVOICETYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

/**
 * Defines one payment method. Directs directly to method without going through PayPage.
 * @author klar-sar
 */
public class PaymentMethodPayment extends HostedPayment<PaymentMethodPayment> {
    
    private PAYMENTMETHOD paymentMethod;
    
    public PaymentMethodPayment(CreateOrderBuilder createOrderBuilder, PAYMENTMETHOD paymentMethod) {
        super(createOrderBuilder);
        this.paymentMethod = paymentMethod;
    }
    
    public PAYMENTMETHOD getPaymentMethod() {
        return paymentMethod;
    }
    
    /**
     * Only used in CardPayment and DirectPayment
     */
    @Override
    protected PaymentMethodPayment configureExcludedPaymentMethods() {
        return this;
    }
    
    @Override
    public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) throws XMLStreamException {
        if (paymentMethod != null) {

    		// invoice -- now swap in country specific invoice/payment plan and write "paymentmethod" to request xml
        	if (paymentMethod == PAYMENTMETHOD.INVOICE) {
                INVOICETYPE invoicetype;
                switch (createOrderBuilder.getCountryCode()) {
                    case DE:
                        invoicetype = INVOICETYPE.INVOICE_DE;
                        break;
                    case DK:
                        invoicetype = INVOICETYPE.INVOICE_DK;
                        break;
                    case FI:
                        invoicetype = INVOICETYPE.INVOICE_FI;
                        break;
                    case NO:
                        invoicetype = INVOICETYPE.INVOICE_NO;
                        break;
                    case NL:
                        invoicetype = INVOICETYPE.INVOICE_NL;
                        break;
                    case SE:
                    default:
                        invoicetype = INVOICETYPE.INVOICE_SE;
                }
                writeSimpleElement(xmlw, "paymentmethod", invoicetype.getValue());
        	// elseif( paymentMethod == PAYMENTMETHOD.PAYMENTPLAN) {	TODO handle paymentplan per country }
        	
            
        	} 
        	// no need to convert payment method
        	else {
        		writeSimpleElement(xmlw, "paymentmethod", paymentMethod.getValue());        
            } 
    	}
		return xmlw;
    }
}
