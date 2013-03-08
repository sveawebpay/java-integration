package se.sveaekonomi.webpay.integration.order.validator;

import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;

public class IdentityValidator {
    private boolean isCompany = false;
    
    public IdentityValidator(boolean isCompany) {
        this.isCompany = isCompany;
    }
 
    
    protected String validateNordicIdentity(CreateOrderBuilder order) {
        String errors = "";
        //check Company identity
        if(isCompany && order.getCompanyCustomer().getNationalIdNumber()==null)
            errors += "MISSING VALUE - OrgNumber is required for company customers when countrycode is SE, NO, DK or FI. Use function setCompanyIdNumber().\n";
        else if(!isCompany && order.getIndividualCustomer().getNationalIdNumber()==null)
            errors += "MISSING VALUE - Ssn is required for individual customers when countrycode is SE, NO, DK or FI. Use function setSsn().\n";
        return errors;
    }
    
    protected String validateNLIdentity(CreateOrderBuilder order) {
        String errors = "";
        //Individual
        if (this.isCompany==false && order.getIndividualCustomer().getInitials()==null )
            errors += "MISSING VALUE - Initials is required for individual customers when countrycode is NL. Use function setInitials().\n";
        if (this.isCompany==false && order.getIndividualCustomer().getBirthDate()<=0)
            errors += "MISSING VALUE - Birth date is required for individual customers when countrycode is NL. Use function setBirthDate().\n";        
        if(this.isCompany == false && (order.getIndividualCustomer().getFirstName() == null || order.getIndividualCustomer().getLastName() == null))            
             errors += "MISSING VALUE - Name is required for individual customers when countrycode is NL. Use function setName().\n";
        //Company
        if (this.isCompany == true && order.getCompanyCustomer().getVatNumber() == null)
            errors += "MISSING VALUE - Vat number is required for company customers when countrycode is NL. Use function setVatNumber().\n";
        if (this.isCompany == true && order.getCompanyCustomer().getCompanyName()==null)
            errors += "MISSING VALUE - Company name is required for individual customers when countrycode is NL. Use function setName().\n";
        //Individual and Company
        if (order.getCustomerIdentity().getStreetAddress()== null || order.getCustomerIdentity().getHouseNumber() <= 0)
            errors += "MISSING VALUE - Street address is required for all customers when countrycode is NL. Use function setStreetAddress().\n";    
        if (order.getCustomerIdentity().getLocality() == null)
            errors += "MISSING VALUE - Locality is required for all customers when countrycode is NL. Use function setLocality().\n";
        if (order.getCustomerIdentity().getZipCode() == null)
            errors += "MISSING VALUE - Zip code is required for all customers when countrycode is NL. Use function setZipCode().\n";
        
        return errors;
                 
    }
    
    protected String validateDEIdentity(CreateOrderBuilder order) {
        String errors = "";
            //Individual
            if (!this.isCompany && !order.getIsCompanyIdentity() && order.getIndividualCustomer().getBirthDate()<=0)
                errors += "MISSING VALUE - Birth date is required for individual customers when countrycode is DE. Use function setBirthDate().\n";
            if (!this.isCompany && !order.getIsCompanyIdentity() && (order.getIndividualCustomer().getFirstName()==null || order.getIndividualCustomer().getLastName()==null))
                errors += "MISSING VALUE - Name is required for individual customers when countrycode is DE. Use function setName().\n";
            //Company
            if (this.isCompany && order.getIsCompanyIdentity() && order.getCompanyCustomer().getVatNumber()==null)
                errors += "MISSING VALUE - Vat number is required for company customers when countrycode is DE. Use function setVatNumber().\n";
            //Individual and Company
            if (order.getCustomerIdentity().getStreetAddress() == null || order.getCustomerIdentity().getHouseNumber() <= 0)
                errors += "MISSING VALUE - Street address is required for all customers when countrycode is DE. Use function setStreetAddress().\n";
            if (order.getCustomerIdentity().getLocality()==null)
                errors += "MISSING VALUE - Locality is required for all customers when countrycode is DE. Use function setLocality().\n";                    
            if (order.getCustomerIdentity().getZipCode()==null)                
                errors += "MISSING VALUE - Zip code is required for all customers when countrycode is DE. Use function setCustomerZipCode().\n";              
        return errors;
    }
     

}
