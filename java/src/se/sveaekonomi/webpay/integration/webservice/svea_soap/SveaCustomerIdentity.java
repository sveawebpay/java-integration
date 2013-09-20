package se.sveaekonomi.webpay.integration.webservice.svea_soap;

import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;


public class SveaCustomerIdentity {
    /**
     * Include NationalIdNumber only in the Nordic countries
     */
    public String NationalIdNumber;
    
    public String Email;
    public String PhoneNumber;
    public String IpAddress;
    public String FullName;
    public String Street;
    public String CoAddress;
    public String ZipCode;
    public String HouseNumber;
    public String Locality;
    public COUNTRYCODE CountryCode;
    public String CustomerType;
    public SveaIdentity IndividualIdentity;
    public SveaIdentity CompanyIdentity;
    
    public SveaCustomerIdentity(SveaIdentity  identity, String key) {
       if (identity != null)
           if (key.equals("IndividualIdentity"))
               IndividualIdentity = identity;
           else
               CompanyIdentity = identity;           
    }
}
