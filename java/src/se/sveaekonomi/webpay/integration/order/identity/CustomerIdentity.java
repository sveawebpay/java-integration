package se.sveaekonomi.webpay.integration.order.identity;


public class CustomerIdentity <T extends CustomerIdentity<T>> {
       
    private String coAddress;
    private String streetAddress;   
    private Integer phoneNumber;
    private String email;
    private String ipAddress;   
    private Integer housenumber;
    private String zipCode;
    private String locality;
  
    public String getEmail() {
        return this.email;
    }
    
    /**
     * Optional but desirable
     * @param type email
     * @return CustomerIdentity
     */
    
    public T setEmail(String email) {
        this.email = email;
        return getGenericThis();
    }
    
    public Integer getPhoneNumber() {
        return this.phoneNumber;
    }
    
    /**
     * Optional
     * @param type phoneNumber
     * @return CustomerIdentity
     */
   
    public T setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
        return getGenericThis();
    }

    @SuppressWarnings("unchecked")
	private T getGenericThis() {
		return (T) this;
	}
    
    public String getZipCode() {
        return zipCode;
    }
    
    /**
     * Required for company and private customers in NL and DE
     * @param type zipCode
     * @return CustomerIdentity
     */   
    public T setZipCode(String zipCode) {
        this.zipCode = zipCode;
        return getGenericThis();
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
        
    public T setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return getGenericThis();
    }
           
    public String getStreetAddress() {
        return streetAddress;
    }
    
    public Integer getHouseNumber() {
        return this.housenumber;
    }
    
    /**
     * Required for company customers in NL and DE
     * @param type streetAddress
     * @param type houseNumber
     * @return CustomerIdentity
     */
    public T setStreetAddress(String streetAddress, Integer houseNumber) {
        this.streetAddress = streetAddress;
        this.housenumber = houseNumber;
        return getGenericThis();
    }
        
    public String getLocality() {
        return locality;
    }
    
    /**
     * Required for company and private customers in NL and DE
     * @param type locality
     * @return CustomerIdentity
     */    
    public T setLocality(String locality) {
        this.locality = locality;
        return getGenericThis();
    }
    
    public String getCoAddress() {
        return coAddress;
    }
      
    public T setCoAddress(String coAddress) {
        this.coAddress = coAddress;
        return getGenericThis();
    }    
}
