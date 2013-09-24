package se.sveaekonomi.webpay.integration.util.constant;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public enum PAYMENTPLANTYPE {
    
    PAYMENTPLAN_SE("SVEASPLITEU_SE", COUNTRYCODE.SE),
    PAYMENTPLANSE("SVEASPLITSE", COUNTRYCODE.SE),
    PAYMENTPLAN_NO("SVEASPLITEU_NO", COUNTRYCODE.NO),
    PAYMENTPLAN_DK("SVEASPLITEU_DK", COUNTRYCODE.DK),
    PAYMENTPLAN_FI("SVEASPLITEU_FI", COUNTRYCODE.FI),
    PAYMENTPLAN_DE("SVEASPLITEU_DE", COUNTRYCODE.DE),
    PAYMENTPLAN_NL("SVEASPLITEU_NL", COUNTRYCODE.NL);
    
    public static final EnumSet<PAYMENTPLANTYPE> ALL_PAYMENTPLANTYPES = EnumSet.allOf(PAYMENTPLANTYPE.class);
    private String value;
    private COUNTRYCODE countryCode;
    
    PAYMENTPLANTYPE(String value, COUNTRYCODE countryCode) {
        this.value = value;
        this.countryCode = countryCode;
    }
    
    public COUNTRYCODE getCountryCode() {
        return this.countryCode;
    }
    
    public String getValue() {
        return value;
    }
    
    public EnumSet<PAYMENTPLANTYPE> getAllInvoiceTypes() {
        return PAYMENTPLANTYPE.ALL_PAYMENTPLANTYPES;
    }
    
    public List<String> getAllPaymentPlanValues() {
        List<String> allValues = new ArrayList<String>();
    
        for (PAYMENTPLANTYPE ppt : PAYMENTPLANTYPE.ALL_PAYMENTPLANTYPES) {
            allValues.add(ppt.getValue());
        }
        return allValues;
    }
}
