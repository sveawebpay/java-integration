package se.sveaekonomi.webpay.integration.order.identity;

public class IndividualCustomer extends CustomerIdentity<IndividualCustomer> {

//-    public $ssn;
//-    public $initials;
//-    public $birthDate;
//-    public $email;
//-    public $phonenumber;
//-    public $ipAddress;
//-    public $firstname;
//-    public $lastname;
//-    public $street;
//-    public $housenumber;
//-    public $coAddress;
//-    public $zipCode;
//-    public $locality;	

    private String ssn;
    private String birthDate;
    private String firstName;
    private String lastName;
    private String initials;
    
    // set by GetOrdersResponse
    private String name;			// compounded fullName from webservice 
	private String streetAddress;	// compounds street + housenumber
    

    public IndividualCustomer() {
        super();
    }
    
    public String getNationalIdNumber() {
        return ssn;
    }
    
    /**
     * Required for private customers in SE, NO, DK, FI
     * @param nationalIdNumber
     * format SE, DK:  yyyymmddxxxx
     * format FI:  ddmmyyxxxx
     * format NO:  ddmmyyxxxxx
     * @return IndividualCustomer
     */
    public IndividualCustomer setNationalIdNumber(String nationalIdNumber) {
        this.ssn = nationalIdNumber;
        return this;
    }
    
    public String getBirthDate() {
        return this.birthDate;
    }
    
    /**
     * Required for private customers in NL and DE
     * @param type yyyy
     * @param type (m)m
     * @param type (d)d
     * @return IndividualCustomer
     * 
     * @deprecated Please use IndividualCustomer.setBirthDate(String date) instead.
     */
    @Deprecated
    public IndividualCustomer setBirthDate(int year, int month, int day) {
        String monthString = String.valueOf(month);
        
        if (monthString.length() == 1) {
            monthString = "0" + monthString;
        }
        
        String dayString = String.valueOf(day);
        
        if (dayString.length() == 1) {
            dayString = "0" + dayString;
        }
        
        this.birthDate = String.valueOf(year) + monthString + dayString;
        
        return this;
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStreetAddress() {
		return streetAddress;
	}

	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

    
    /**
     * Required for private customers in NL and DE
     * @param type yyyy(m)m(d)d
     * 
     * @return IndividualCustomer
     */
    public IndividualCustomer setBirthDate(String date) {
        this.birthDate = date; 
        return this;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Required for private Customers in NL and DE
     * @param type firstName
     * @param type lastName
     * @return IndividualCustomer
     */
    public IndividualCustomer setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }
     
    public String getInitials() {
        return this.initials;
    }
    
    /**
     * Required for private customers in NL 
     * @param type initials
     * @return IndividualCustomer
     */
    public IndividualCustomer setInitials(String initials) {
        this.initials = initials;
        return this;
    }
}
