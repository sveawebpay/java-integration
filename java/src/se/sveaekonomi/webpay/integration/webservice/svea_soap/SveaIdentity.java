package se.sveaekonomi.webpay.integration.webservice.svea_soap;

public class SveaIdentity {
    
    public String FirstName;
    public String LastName;
    public String Initials;
    public String BirthDate;
    public String OrgNumber;
    public String CompanyVatNumber;
    
    public SveaIdentity(boolean isCompany) {
        //Individual
        if (isCompany) {
            FirstName = "";
            LastName = "";
            Initials = "";
            BirthDate = "";
        }
        //Company
        else {
            OrgNumber = "";
            CompanyVatNumber = "";
        }
    }
}
