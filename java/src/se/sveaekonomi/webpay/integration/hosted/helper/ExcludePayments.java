package se.sveaekonomi.webpay.integration.hosted.helper;

import java.util.ArrayList;
import java.util.List;

import se.sveaekonomi.webpay.integration.util.constant.INVOICETYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTPLANTYPE;

public class ExcludePayments {
    
    private ArrayList<String> excludedPaymentMethods;
    
    public ExcludePayments() {
        excludedPaymentMethods = new ArrayList<String>();
    }
    
    /**
     * @return List of all payment methods for invoices and payment plans
     */
    public List<String> excludeInvoicesAndPaymentPlan() {
        setPaymentMethodsSE();
        setPaymentMethodsDE();
        setPaymentMethodsDK();
        setPaymentMethodsFI();
        setPaymentMethodsNL();
        setPaymentMethodsNO();
        
        return excludedPaymentMethods;
    }
    
    private void setPaymentMethodsSE() {
        excludedPaymentMethods.add(INVOICETYPE.INVOICESE.getValue());
        excludedPaymentMethods.add(INVOICETYPE.INVOICE_SE.getValue());
        excludedPaymentMethods.add(PAYMENTPLANTYPE.PAYMENTPLANSE.getValue());
        excludedPaymentMethods.add(PAYMENTPLANTYPE.PAYMENTPLAN_SE.getValue());
    }
    
    private void setPaymentMethodsDE() {
        excludedPaymentMethods.add(INVOICETYPE.INVOICE_DE.getValue());
        excludedPaymentMethods.add(PAYMENTPLANTYPE.PAYMENTPLAN_DE.getValue());
    }
    
    private void setPaymentMethodsDK() {
         excludedPaymentMethods.add(INVOICETYPE.INVOICE_DK.getValue());
         excludedPaymentMethods.add(PAYMENTPLANTYPE.PAYMENTPLAN_DK.getValue());
    }
    
    private void setPaymentMethodsFI() {
        excludedPaymentMethods.add(INVOICETYPE.INVOICE_FI.getValue());
        excludedPaymentMethods.add(PAYMENTPLANTYPE.PAYMENTPLAN_FI.getValue());
    }
    
    private void setPaymentMethodsNL() {
         excludedPaymentMethods.add(INVOICETYPE.INVOICE_NL.getValue());
         excludedPaymentMethods.add(PAYMENTPLANTYPE.PAYMENTPLAN_NL.getValue());
    }
    
    private void setPaymentMethodsNO() {
        excludedPaymentMethods.add(INVOICETYPE.INVOICE_NO.getValue());
        excludedPaymentMethods.add(PAYMENTPLANTYPE.PAYMENTPLAN_NO.getValue());
    }
}
