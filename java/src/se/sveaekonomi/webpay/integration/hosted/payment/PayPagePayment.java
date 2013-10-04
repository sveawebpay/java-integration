package se.sveaekonomi.webpay.integration.hosted.payment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import se.sveaekonomi.webpay.integration.hosted.helper.ExcludePayments;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.INVOICETYPE;
import se.sveaekonomi.webpay.integration.util.constant.LANGUAGECODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTPLANTYPE;

/**
 * Defines specific payment methods to be shown in PayPage
 * @author klar-sar
 */
public class PayPagePayment extends HostedPayment<PayPagePayment> {

    protected String paymentMethod;
    protected List<String> includedPaymentMethods;

    public PayPagePayment(CreateOrderBuilder orderBuilder) {
        super(orderBuilder);
        includedPaymentMethods = new ArrayList<String>();
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public PayPagePayment setPaymentMethod(PAYMENTMETHOD paymentMethod) {
        if (paymentMethod.equals(PAYMENTMETHOD.INVOICE)) {
            this.paymentMethod = getValidInvoiceTypeForIncludedList();
        } else if (paymentMethod.equals(PAYMENTMETHOD.PAYMENTPLAN)) {
            this.paymentMethod = getValidPaymentPlanTypeForIncludedList();
        } else {
            this.paymentMethod = paymentMethod.getValue();
        }
        
        return this;
    }

    public List<String> getIncludedPaymentMethods() {
        return includedPaymentMethods;
    }

    /**
     * Only used in CardPayment and DirectPayment
     */
    protected PayPagePayment configureExcludedPaymentMethods() {
        return this;
    }

    public PayPagePayment excludeCardPaymentMethods() {
        excludedPaymentMethods.add(PAYMENTMETHOD.KORTCERT.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL.getValue());
        
        return this;
    }

    public PayPagePayment excludeDirectPaymentMethods() {
        excludedPaymentMethods.add(PAYMENTMETHOD.NORDEA_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SEB_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SEBFTG_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SHB_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SWEDBANK_SE.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.BANKAXESS.getValue());
        
        return this;
    }

    public PayPagePayment excludePaymentMethods(Collection<PAYMENTMETHOD> paymentMethods) {
        addCollectionToExcludePaymentMethodsList(paymentMethods);
        return this;
    }
    
    private void addCollectionToExcludePaymentMethodsList(
        Collection<PAYMENTMETHOD> paymentMethods) {
        for (PAYMENTMETHOD pm : paymentMethods) {
            
            if (pm.equals(PAYMENTMETHOD.INVOICE)) {
                excludedPaymentMethods.addAll(INVOICETYPE.INVOICE_SE.getAllInvoiceValues());
            } else if (pm.equals(PAYMENTMETHOD.PAYMENTPLAN)) {
                excludedPaymentMethods.addAll(PAYMENTPLANTYPE.PAYMENTPLAN_SE.getAllPaymentPlanValues());
            } else {
                excludedPaymentMethods.add(pm.getValue());
            }
        }
    }
    
    public PayPagePayment excludePaymentMethods() {
        List<PAYMENTMETHOD> emptyList = new ArrayList<PAYMENTMETHOD>();
        return excludePaymentMethods(emptyList);
    }
    
    public PayPagePayment includePaymentMethods() {
        List<PAYMENTMETHOD> emptyList = new ArrayList<PAYMENTMETHOD>();
        return includePaymentMethods(emptyList);
    }
    
    public PayPagePayment includePaymentMethods(Collection<PAYMENTMETHOD> paymentMethods) {
        addCollectionToIncludedPaymentMethodsList(paymentMethods);
        
        // Exclude all payment methods
        ExcludePayments excluded = new ExcludePayments();
        excludedPaymentMethods = excluded.excludeInvoicesAndPaymentPlan();
        excludedPaymentMethods.add(PAYMENTMETHOD.KORTCERT.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.SKRILL.getValue());
        excludedPaymentMethods.add(PAYMENTMETHOD.PAYPAL.getValue());
        excludeDirectPaymentMethods();
        
        // Remove the included methods from the excluded payment methods
        for (String pm : includedPaymentMethods) {
            excludedPaymentMethods.remove(pm);
        }
        
        return this;
    }
    
    private String getValidPaymentPlanTypeForIncludedList() {
        for (PAYMENTPLANTYPE ppt : PAYMENTPLANTYPE.ALL_PAYMENTPLANTYPES) {
            //never include from old flow to include list - won´t show in paypage
            if (createOrderBuilder.getCountryCode().equals(COUNTRYCODE.SE)
                    && ppt.equals(PAYMENTPLANTYPE.PAYMENTPLANSE)) {
                continue;
            }
            //include only Payment plan for current country 
            else if (ppt.getCountryCode().equals(createOrderBuilder.getCountryCode())) {
                return ppt.getValue();
            }
        }
        
        return "";
    }
    
    private String getValidInvoiceTypeForIncludedList() {
        for (INVOICETYPE it : INVOICETYPE.ALL_INVOICETYPES) {
            //never include old flow to include list
            if (createOrderBuilder.getCountryCode().equals(COUNTRYCODE.SE)
                    && it.equals(INVOICETYPE.INVOICESE)) {
                continue;
            } else if (it.getCountryCode().equals(createOrderBuilder.getCountryCode())) {
                return it.getValue();
            }
        }
        
        return "";
    }
    
    private void addCollectionToIncludedPaymentMethodsList(Collection<PAYMENTMETHOD> paymentMethods) {
        for (PAYMENTMETHOD pm : paymentMethods) {
            if (pm.equals(PAYMENTMETHOD.INVOICE)) {
                includedPaymentMethods.add(getValidInvoiceTypeForIncludedList());
            } else if (pm.equals(PAYMENTMETHOD.PAYMENTPLAN)) {
                includedPaymentMethods.add(getValidPaymentPlanTypeForIncludedList());
            } else {
                includedPaymentMethods.add(pm.getValue());
            }
        }
    }
    
    public PayPagePayment setPayPageLanguage(LANGUAGECODE languageCode) {
        this.languageCode = languageCode.toString();
        
        return this;
    }
    
    public XMLStreamWriter getPaymentSpecificXml(XMLStreamWriter xmlw) throws XMLStreamException {
        if (paymentMethod!=null) {
            writeSimpleElement(xmlw, "paymentmethod", paymentMethod);
        }
        
        return xmlw;
    }
}
