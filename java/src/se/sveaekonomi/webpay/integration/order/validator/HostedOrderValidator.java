package se.sveaekonomi.webpay.integration.order.validator;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;


public class HostedOrderValidator extends OrderValidator {
          
    public String validate(CreateOrderBuilder order) {
        errors = "";     
        
        if(order.getCountryCode()==null)
        	errors += "MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n";
        else if(order.getCountryCode()!=null && order.getCountryCode()==COUNTRYCODE.NL)
            errors += new IdentityValidator().validateNLIdentity(order);
        else if(order.getCountryCode()!=null && order.getCountryCode()==COUNTRYCODE.DE)
            errors += new IdentityValidator().validateDEIdentity(order);
        else
            errors += new IdentityValidator().validateNordicIdentity(order);
        validateClientOrderNumber(order);
        validateCurrency(order);
        validateRequiredFieldsForOrder(order);
        validateOrderRow(order);
        return this.errors;
    }
    
    private void validateCurrency(CreateOrderBuilder order) {
        if(order.getCurrency()==null)
            errors += "MISSING VALUE - Currency is required. Use setCurrency(...).\n";
    }

    private void validateClientOrderNumber(CreateOrderBuilder order) {
        if (order.getClientOrderNumber() == null) { 
            this.errors += "MISSING VALUE - ClientOrderNumber is required. Use setClientOrderNumber(...).\n";            
        }        
        else if((order.getClientOrderNumber() != null && "".equals(order.getClientOrderNumber())))
            this.errors += "MISSING VALUE - ClientOrderNumber is required (has an empty value). Use setClientOrderNumber(...).\n";
    }
}
