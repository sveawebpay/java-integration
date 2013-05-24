package se.sveaekonomi.webpay.integration.response.webservice;

public class CustomerIdentity {
    
    private String nationalIdNumber;
    private String fullName;
    private String intitials;
    private String coAddress;
    private String street;
    private String zipCode;
    private String city;
    private String countryCode;
    private String phoneNumber;
    private String email;
    private String customerType;
    private String ipAddress;
    private String houseNumber;
    
    public void setValue(String name, String value) {
        if (name.equals("NationalIdNumber"))
            this.setNationalIdNumber(value);
        else if (name.equals("Email"))
            this.setEmail(value);
        else if (name.equals("PhoneNumber"))
            this.setPhoneNumber(value);
   //     else if (name.equals("IpAddress"))
   //         this.setIpAddress(value);
        else if (name.equals("FullName"))
            this.setFullName(value);
        else if (name.equals("Street"))
            this.setStreet(value);
        else if (name.equals("CoAddress"))
            this.setCoAddress(value);
        else if (name.equals("ZipCode"))
            this.setZipCode(value);
        else if (name.equals("HouseNumber"))
            this.setHouseNumber(value);
        else if (name.equals("Locality"))
            this.setCity(value);
        else if (name.equals("CountryCode"))
            this.setCountryCode(value);
        else if (name.equals("CustomerType"))
            this.setCustomerType(value);
    }

    public String getNationalIdNumber() {
        return nationalIdNumber;
    }

    public void setNationalIdNumber(String nationalIdNumber) {
        this.nationalIdNumber = nationalIdNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIntitials() {
        return intitials;
    }

    public void setIntitials(String intitials) {
        this.intitials = intitials;
    }

    public String getCoAddress() {
        return coAddress;
    }

    public void setCoAddress(String coAddress) {
        this.coAddress = coAddress;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }
}
