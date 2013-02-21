package se.sveaekonomi.webpay.integration.order.validator;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;


public class HostedOrderValidator extends OrderValidator {
          
    public String validate(CreateOrderBuilder order) {
        errors = "";
        Boolean isCompany = order.getCompanyCustomer().getCompanyIdNumber()!=null ||order.getCompanyCustomer().getVatNumber()!=null;
        if(order.getCountryCode()!=null && order.getCountryCode()==COUNTRYCODE.NL)
            errors += new IdentityValidator(isCompany).validateNLIdentity(order);
        else if(order.getCountryCode()!=null && order.getCountryCode()==COUNTRYCODE.DE)
            errors += new IdentityValidator(isCompany).validateDEIdentity(order);
        else
            errors += new IdentityValidator(isCompany).validateNordicIdentity(order);
        validateClientOrderNumber(order);
        validateCurrency(order);
        validateRequiredFieldsForOrder(order);
        validateOrderRow(order);
        return this.errors;
    }
    
    private void validateCurrency(CreateOrderBuilder order) {
        if(order.getCurrency()==null)
            errors += "MISSING VALUE - Currency is required. Use function setCurrency().\n";
    }

    private void validateClientOrderNumber(CreateOrderBuilder order) {
        if (order.getClientOrderNumber() == null) { 
            this.errors += "MISSING VALUE - ClientOrderNumber is required. Use function setClientOrderNumber().\n";            
        }        
        else if((order.getClientOrderNumber() != null && "".equals(order.getClientOrderNumber())))
            this.errors += "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use function setClientOrderNumber().\n";
    }
}
