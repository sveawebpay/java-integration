package se.sveaekonomi.webpay.integration.order.validator;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class WebServiceOrderValidator extends OrderValidator {
    
    protected boolean isCompany = false;
    
    public String validate(CreateOrderBuilder order) {
        try {
            if (order.getCustomerIdentity() == null) {
                errors += "MISSING VALUE - CustomerIdentity must be set.\n";
            }
            
            if (order.getIsCompanyIdentity() && (order.getCompanyCustomer().getNationalIdNumber() != null
                    || order.getCompanyCustomer().getVatNumber() != null
                    || order.getCompanyCustomer().getCompanyName() != null)) {
                isCompany = true;
            }
            
            IdentityValidator identityValidator = new IdentityValidator();
            
            if (order.getCountryCode() != null) {
                if (order.getCountryCode().equals(COUNTRYCODE.SE)
                        || order.getCountryCode().equals(COUNTRYCODE.NO)
                        || order.getCountryCode().equals(COUNTRYCODE.DK)
                        || order.getCountryCode().equals(COUNTRYCODE.FI)) {
                    this.errors += identityValidator.validateNordicIdentity(order);
                } else if (order.getCountryCode().equals(COUNTRYCODE.DE)) {
                    this.errors += identityValidator.validateDEIdentity(order);
                } else if (order.getCountryCode().equals(COUNTRYCODE.NL)) {
                    this.errors += identityValidator.validateNLIdentity(order);
                } else {
                    this.errors += "NOT VALID - Given countrycode does not exist in our system.\n";
                }
            } else {
                this.errors += "MISSING VALUE - CountryCode is required. Use setCountryCode().\n";
            }
            
            validateRequiredFieldsForOrder(order);
            validateOrderRow(order);
            
            if (order.getOrderDate() == null) {
                this.errors += "MISSING VALUE - OrderDate is required. Use setOrderDate().\n";
            }
        } catch (NullPointerException e) {
            errors += "MISSING VALUE - CustomerIdentity must be set.\n";
        }
        
        return this.errors;
    }
}
