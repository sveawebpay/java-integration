package se.sveaekonomi.webpay.integration.hosted.helper;

import java.util.ArrayList;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;


public class ExcludePayments {
    
    private ArrayList<PAYMENTMETHOD> excludedPaymentMethods;
    
    
    public ExcludePayments() {
        excludedPaymentMethods = new ArrayList<PAYMENTMETHOD>();
    }
    
    public ArrayList<PAYMENTMETHOD> excludeInvoicesAndPaymentPlan() {
   
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICESE);
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITSE);
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_SE);
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_SE);
            
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_DE);
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_DE);

            excludedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_DK);
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_DK);

            excludedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_FI);
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_FI);

            excludedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_NL);
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_NL);

            excludedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_NO);
            excludedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_NO);

        return excludedPaymentMethods;
    }
    
}
