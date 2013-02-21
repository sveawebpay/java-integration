package se.sveaekonomi.webpay.integration.order.identity;


public class CompanyCustomer extends CustomerIdentity<CompanyCustomer> {
    
    private String companyName;
    private String orgNumber;
    private String companyVatNumber;

    public CompanyCustomer() {
        super();
    }
    
    public String getCompanyName() {
        return this.companyName;
    }
    
    public CompanyCustomer setCompanyName(String name) {
        this.companyName = name;
        return this;
    }
    
    public String getCompanyIdNumber() {
        return orgNumber;
    }

    public CompanyCustomer setCompanyIdNumber(String companyIdNumber) {
        this.orgNumber = companyIdNumber;
        return this;
    }
    
    public String getVatNumber() {
        return this.companyVatNumber;
    }
    
    public CompanyCustomer setVatNumber(String vatNumber) {
        this.companyVatNumber = vatNumber;
        return this;
    }
}
