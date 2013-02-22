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
    
    @SuppressWarnings("unchecked")
    public T setEmail(String email) {
        this.email = email;
        return (T) this;
    }
    
    public Integer getPhoneNumber() {
        return this.phoneNumber;
    }
    
    @SuppressWarnings("unchecked")
    public T setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
        return (T) this;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    @SuppressWarnings("unchecked")
    public T setZipCode(String zipCode) {
        this.zipCode = zipCode;
        return (T) this;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    @SuppressWarnings("unchecked")
    public T setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return (T) this;
    }
           
    public String getStreetAddress() {
        return streetAddress;
    }
    
    public Integer getHouseNumber() {
        return this.housenumber;
    }
    
    @SuppressWarnings("unchecked")
    public T setStreetAddress(String streetAddress, Integer houseNumber) {
        this.streetAddress = streetAddress;
        this.housenumber = houseNumber;
        return (T) this;
    }
        
    public String getLocality() {
        return locality;
    }
    
    @SuppressWarnings("unchecked")
    public T setLocality(String locality) {
        this.locality = locality;
        return (T) this;
    }
    
    public String getCoAddress() {
        return coAddress;
    }
    
    @SuppressWarnings("unchecked")
    public T setCoAddress(String coAddress) {
        this.coAddress = coAddress;
        return (T) this;
    }    
}
