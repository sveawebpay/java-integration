package se.sveaekonomi.webpay.integration.hosted.payment;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.INVOICETYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTPLANTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

/**
 * Defines one payment method. Directs directly to method without going through PayPage.
 * @author klar-sar, Kristian Grossman-Madsen
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

    		// invoice -- now swap in country specific invoice/partpayment paymentmethod code and write to request xml
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
        	} 
        	// handle partpayment per country
        	else if(paymentMethod == PAYMENTMETHOD.PAYMENTPLAN) {
                PAYMENTPLANTYPE paymentplantype;
                switch (createOrderBuilder.getCountryCode()) {
                    case DE:
                    	paymentplantype = PAYMENTPLANTYPE.PAYMENTPLAN_DE;
                        break;
                    case DK:
                    	paymentplantype = PAYMENTPLANTYPE.PAYMENTPLAN_DK;
                        break;
                    case FI:
                    	paymentplantype = PAYMENTPLANTYPE.PAYMENTPLAN_FI;
                        break;
                    case NO:
                    	paymentplantype = PAYMENTPLANTYPE.PAYMENTPLAN_NO;
                        break;
                    case NL:
                    	paymentplantype = PAYMENTPLANTYPE.PAYMENTPLAN_NL;
                        break;
                    case SE:
                    default:
                    	paymentplantype = PAYMENTPLANTYPE.PAYMENTPLAN_SE;
                }
                writeSimpleElement(xmlw, "paymentmethod", paymentplantype.getValue());
        	} 
        	// no need to convert payment method
        	else {
        		writeSimpleElement(xmlw, "paymentmethod", paymentMethod.getValue());        
            } 
    	}
		return xmlw;
    }
}
