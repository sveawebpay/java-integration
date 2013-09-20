package se.sveaekonomi.webpay.integration.order.identity;

public class CompanyCustomer extends CustomerIdentity<CompanyCustomer> {
    
    private String companyName;
    private String orgNumber;
    private String companyVatNumber;
    private String addressSelector;
    
    public CompanyCustomer() {
        super();
    }
    
    public String getCompanyName() {
        return this.companyName;
    }
    
    /**
     * Required for Eu countries like NL and DE
     * @param type name
     * @return CompanyCustomer
     */
    public CompanyCustomer setCompanyName(String name) {
        this.companyName = name;
        return this;
    }
    
    public String getNationalIdNumber() {
        return orgNumber;
    }
    
    /**
     * Example: 4608142222
     * Required for company customers in SE, NO, DK, FI
     * For SE: Organisationsnummer
     * For NO: Organisasjonsnummer
     * For DK: CVR
     * For FI: Yritystunnus
     * @param type companyIdNumber
     * @return CompanyCustomer
     */
    public CompanyCustomer setNationalIdNumber(String companyIdNumber) {
        this.orgNumber = companyIdNumber;
        return this;
    }
    
    public String getVatNumber() {
        return this.companyVatNumber;
    }
    
    /**
     * Example: NL123456789A12
     * @param type vatNumber
     * Required for NL and DE
     * @return CompanyCustomer
     */
    public CompanyCustomer setVatNumber(String vatNumber) {
        this.companyVatNumber = vatNumber;
        return this;
    }
    
    /**
     * 
     * @param addressSelector
     * @return CompanyCustomer
     */
	public CompanyCustomer setAddressSelector(String addressSelector) {
		this.addressSelector = addressSelector;
		return this;
	}
	
	public String getAddressSelector() {
		return this.addressSelector;
	}
}
