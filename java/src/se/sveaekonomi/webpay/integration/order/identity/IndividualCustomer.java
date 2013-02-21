package se.sveaekonomi.webpay.integration.order.identity;


public class IndividualCustomer extends CustomerIdentity<IndividualCustomer> {
    private long ssn;
    private long birthDate;
    private String firstName;
    private String lastName;
    private String initials;
    
    public IndividualCustomer() {
        super();
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public IndividualCustomer setName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        return this;
    }
    
    public long getSsn() {
        return ssn;
    }

    public IndividualCustomer setSsn(long yyyymmddxxxx) {
        this.ssn = yyyymmddxxxx;
        return this;
    }
     
    public String getInitials() {
        return this.initials;
    }
    
    public IndividualCustomer setInitials(String initials) {
        this.initials = initials;
        return this;
    }
    
    public long getBirthDate() {
        return this.birthDate;
    }    
    
    public IndividualCustomer setBirthDate(int year, int month, int day) {
        String monthString = String.valueOf(month);        
        if(monthString.length()==1)
            monthString = "0" + monthString;
        String dayString = String.valueOf(day);
        if(dayString.length()==1)
            dayString = "0" + dayString;
        String s = String.valueOf(year) + monthString + dayString;
        this.birthDate = Long.parseLong(s);
        return this;
    }
}
