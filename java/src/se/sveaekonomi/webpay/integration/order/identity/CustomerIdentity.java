package se.sveaekonomi.webpay.integration.order.identity;

public abstract class CustomerIdentity <T extends CustomerIdentity<T>> {

    private String phoneNumber;
    private String email;
    private String ipAddress;
    private String coAddress;
    private String streetAddress;
    private String housenumber;
    private String zipCode;
    private String locality;
    
    @SuppressWarnings("unchecked")
    private T getGenericThis() {
        return (T) this;
    }
    
    abstract public String getNationalIdNumber();
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    /**
     * Optional
     * @param type phoneNumber
     * @return CustomerIdentity
     */
    public T setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return getGenericThis();
    }
    
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
    
    /**
     * Required for customers in NL and DE
     * @param type streetAddress
     * @param type houseNumber
     * @return CustomerIdentity
     */
    public T setStreetAddress(String streetAddress, String houseNumber) {
        this.streetAddress = streetAddress;
        this.housenumber = houseNumber;
        return getGenericThis();
    }
    
    /** Use for streetAddresses without separate HouseNumber */
    public T setStreetAddress(String streetAddress) {
        return setStreetAddress(streetAddress, null);
    }
    
    public String getCoAddress() {
        return coAddress;
    }
      
    public T setCoAddress(String coAddress) {
        this.coAddress = coAddress;
        return getGenericThis();
    }
    
    public String getHouseNumber() {
        return this.housenumber;
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
}
