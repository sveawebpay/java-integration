package se.sveaekonomi.webpay.integration.order.validator;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;

public class IdentityValidator {
    
    protected String validateNordicIdentity(CreateOrderBuilder order) {
        String errors = "";
        //check Company identity
        boolean isCompanyIdentity = order.getIsCompanyIdentity();
        if (isCompanyIdentity && order.getCompanyCustomer().getNationalIdNumber()==null)
            errors += "MISSING VALUE - Organisation number is required for company customers when countrycode is SE, NO, DK or FI. Use setNationalIdNumber(...).\n";
        else if (!isCompanyIdentity && order.getIndividualCustomer().getNationalIdNumber()==null)
            errors += "MISSING VALUE - National number(ssn) is required for individual customers when countrycode is SE, NO, DK or FI. Use setNationalIdNumber(...).\n";
        return errors;
    }
    
    public String validateNLIdentity(CreateOrderBuilder order) {
        String errors = "";
        //Individual
        if (!order.getIsCompanyIdentity() && order.getIndividualCustomer().getInitials()==null )
            errors += "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use setInitials().\n";
        if (!order.getIsCompanyIdentity() && order.getIndividualCustomer().getBirthDate()<=0)
            errors += "MISSING VALUE - Birth date is required for individual customers when countrycode is NL. Use setBirthDate().\n";        
        if (!order.getIsCompanyIdentity() && (order.getIndividualCustomer().getFirstName() == null || order.getIndividualCustomer().getLastName() == null))            
             errors += "MISSING VALUE - Name is required for individual customers when countrycode is NL. Use setName().\n";
        //Company
        if (order.getIsCompanyIdentity() && order.getCompanyCustomer().getVatNumber() == null)
            errors += "MISSING VALUE - Vat number is required for company customers when countrycode is NL. Use setVatNumber().\n";
        if (order.getIsCompanyIdentity() && order.getCompanyCustomer().getCompanyName()==null)
            errors += "MISSING VALUE - Company name is required for individual customers when countrycode is NL. Use setName().\n";
        //Individual and Company
        if (order.getCustomerIdentity().getStreetAddress()== null || order.getCustomerIdentity().getHouseNumber() == null)
            errors += "MISSING VALUE - Street address and house number is required for all customers when countrycode is NL. Use setStreetAddress().\n";    
        if (order.getCustomerIdentity().getLocality() == null)
            errors += "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use setLocality().\n";
        if (order.getCustomerIdentity().getZipCode() == null)
            errors += "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use setZipCode().\n";
        
        return errors;
        
    }
    
    public String validateDEIdentity(CreateOrderBuilder order) {
        String errors = "";
            //Individual
            if (!order.getIsCompanyIdentity() && !order.getIsCompanyIdentity() && order.getIndividualCustomer().getBirthDate()<=0)
                errors += "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use setBirthDate().\n";
            if (!order.getIsCompanyIdentity() && !order.getIsCompanyIdentity() && (order.getIndividualCustomer().getFirstName()==null || order.getIndividualCustomer().getLastName()==null))
                errors += "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use setName().\n";
            //Company
            if (order.getIsCompanyIdentity() && order.getIsCompanyIdentity() && order.getCompanyCustomer().getVatNumber()==null)
                errors += "MISSING VALUE - Vat number is required for company customers when countrycode is DE. Use setVatNumber().\n";
            //Individual and Company
            if (order.getCustomerIdentity().getStreetAddress() == null || order.getCustomerIdentity().getHouseNumber() == null)
                errors += "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use setStreetAddress().\n";
            if (order.getCustomerIdentity().getLocality()==null)
                errors += "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use setLocality().\n";                    
            if (order.getCustomerIdentity().getZipCode()==null)
                errors += "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use setCustomerZipCode().\n";              
        return errors;
    }
}
