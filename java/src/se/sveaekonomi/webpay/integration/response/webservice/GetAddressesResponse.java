package se.sveaekonomi.webpay.integration.response.webservice;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.response.Response;

/**
 *	// The following CompanyCustomer fields are set for GetAddresses, company customer:
 *
 *	assertEquals("Testgatan 1", address.getStreetAddress() );
 *	assertEquals("c/o Eriksson, Erik", address.getCoAddress() );
 *	assertEquals( "99999", address.getZipCode() );
 *	assertEquals( "Stan", address.getLocality() );
 *	assertEquals( "08 - 111 111 11", address.getPhoneNumber() );
 *	assertEquals( "Persson, Tess T", address.getCompanyName() );
 *	assertEquals( "4608142222", address.getNationalIdNumber() );
 *	assertEquals( "5F445B19E8C87954904FB7531A51AEE57C5E9413", address.getAddressSelector() );
 *	// Fields in  CompanyCustomer that are not available in GetAddresses request response
 *	// from CustomerIdentity
 *	assertEquals( null, address.getIpAddress() );
 *	assertEquals( null, address.getEmail() );
 *	assertEquals( null, address.getHouseNumber() );
 *	// from CompanyCustomer
 *	assertEquals( null, address.getVatNumber() );
 *	// Fields in GetAddresses request response that have no counterpart in CompanyCustomer, FYI:
 *	// <BusinessType>Business</BusinessType>
 *	// <FirstName>Tess T</FirstName>
 *	// <LastName>Persson</LastName>
 *
 *
 *  // The following IndividualCustomer fields are set for GetAddresses, individual customer:
 *  
 *	assertEquals("Testgatan 1", address.getStreetAddress() );
 *	assertEquals("c/o Eriksson, Erik", address.getCoAddress() );
 *	assertEquals( "99999", address.getZipCode() );
 *	assertEquals( "Stan", address.getLocality() );
 *	assertEquals( "08 - 111 111 11", address.getPhoneNumber() );
 *	assertEquals( "4605092222", address.getNationalIdNumber() );
 *	assertEquals( "Tess T", address.getFirstName() );
 *	assertEquals( "Persson", address.getLastName() );        
 *	// Fields in  IndividualCustomer that are not available in GetAddresses request response
 *	// from CustomerIdentity
 *	assertEquals( null, address.getIpAddress() );
 *	assertEquals( null, address.getEmail() );
 *	// from IndividualIdentity
 *	assertEquals( null, address.getHouseNumber() );
 *	assertEquals( null, address.getInitials() );
 *	assertEquals( null, address.getBirthDate() );
 *	// Fields in GetAddresses request response that have no counterpart in IndividualCustomer, FYI:
 *	// <LegalName>Persson, Tess T</LegalName>
 *	// <BusinessType>Person</BusinessType>
 *	// <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
 *  
 * 
 * @author Kristian Grossman-Madsen
 */
public class GetAddressesResponse extends WebServiceResponse {

	@SuppressWarnings("rawtypes")
	private ArrayList<CustomerIdentity> customerAddresses;	
	
    @SuppressWarnings("rawtypes")
	public GetAddressesResponse(NodeList soapMessage) {
        super(soapMessage); // handle ErrorMessage, ResultCode
        this.customerAddresses = new ArrayList<CustomerIdentity>(); 
    	if( this.isOrderAccepted() ) {
            setValues(soapMessage);
    	}        
    }

	/** deprecated & will return concatenation of LastName and FirstName for individual customers (i.e. BusinessType = "Person") from 1.6.1 on */
    public String getLegalName() {
    	String legalName = "";
    	CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
    	if( firstAddress instanceof IndividualCustomer ) {
    		legalName = new String(((IndividualCustomer)firstAddress).getLastName()).concat(", ").concat(((IndividualCustomer)firstAddress).getFirstName());
    	}    	
    	if( firstAddress instanceof CompanyCustomer ) {
    		legalName = ((CompanyCustomer) firstAddress).getCompanyName();
    	}
    	return legalName;
	}    

	/** deprecated from 1.6.1 on */
    public String getSecurityNumber() {
    	CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
    	return firstAddress.getNationalIdNumber();
    }
        
	/** deprecated from 1.6.1 on */
    public String getAddressLine1() {
    	CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
    	return firstAddress.getCoAddress();
	}
    
	/** deprecated from 1.6.1 on */
    public String getAddressLine2() {
		CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
		return firstAddress.getStreetAddress();
	}

	/** deprecated from 1.6.1 on */
    public String getPostcode() {
		CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
		return firstAddress.getZipCode();
	}

	/** deprecated from 1.6.1 on */
    public String getPostarea() {
		CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
		return firstAddress.getLocality();
	}

	/** deprecated from 1.6.1 on */
    public String getBusinessType() {
    	String businessType = "";
    	CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
    	if( firstAddress instanceof IndividualCustomer ) {
    		businessType = "Person";
    	}    	
    	if( firstAddress instanceof CompanyCustomer ) {
    		businessType = "Business";
    	}
    	return businessType;
	} 
    
    /** deprecated & will return null for individual customers (i.e. BusinessType = "Person") from 1.6.1 on */
    public String getAddressSelector() {
    	String addressSelector = "";
    	CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
    	if( firstAddress instanceof IndividualCustomer ) {
    		addressSelector = null;
    	}  
    	if( firstAddress instanceof CompanyCustomer ) {
    		addressSelector = ((CompanyCustomer) firstAddress).getAddressSelector();
    	}  
    	return addressSelector;
    }
    
    /** deprecated & will return null for company customers (i.e. BusinessType = "Business") from 1.6.1 on */
    public String getFirstName() {
    	String firstName = "";
		CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
    	if( firstAddress instanceof IndividualCustomer ) {
    		firstName = ((IndividualCustomer) firstAddress).getFirstName();
    	}  
    	if( firstAddress instanceof CompanyCustomer ) {
    		firstName = null;
    	}
		return firstName;
    }
    
    /** deprecated, will return null for company customers (i.e. BusinessType = "Business") from 1.6.1 on */
    public String getLastName() {
    	String lastName = "";
		CustomerIdentity<?> firstAddress = this.customerAddresses.get(0);
    	if( firstAddress instanceof IndividualCustomer ) {
    		lastName = ((IndividualCustomer) firstAddress).getLastName();
    	}  
    	if( firstAddress instanceof CompanyCustomer ) {
    		lastName = null;
    	}
		return lastName;
    }
        	
	@SuppressWarnings("unchecked")
	private <T> T getAddresses() {
		return (T) this.customerAddresses;
	}
	
	public ArrayList<IndividualCustomer> getIndividualAddresses() {
		return getAddresses();
	}
	
	public ArrayList<CompanyCustomer> getCompanyAddresses() {
		return getAddresses();
	}
	    
    private void setValues(NodeList soapMessage) {
		
		// <GetAddressesResponse xmlns="https://webservices.sveaekonomi.se/webpay">
		Node getAddressesResponse = soapMessage.item(0);
		//     <GetAddressesResult>
		Node getAddressesResult = getAddressesResponse.getChildNodes().item(0);
		//        <Accepted>true</Accepted>
		//        <RejectionCode>Accepted</RejectionCode>
		//        <Addresses>
		Node addresses = getAddressesResult.getChildNodes().item(2);		// 0: Accepted, 1: RejectionCode
		//           <CustomerAddress>
		NodeList customerAddress = addresses.getChildNodes();
		for( int i=0; i < customerAddress.getLength(); i++ ) {
			Element ca = (Element) customerAddress.item(i);				
			//              <LegalName>Persson, Tess T</LegalName>
			String caLegalName = (ca.getElementsByTagName("LegalName").item(0) == null) ? null : ca.getElementsByTagName("LegalName").item(0).getTextContent();			
			//              <SecurityNumber>4608142222</SecurityNumber>
			String caSecurityNumber = (ca.getElementsByTagName("SecurityNumber").item(0) == null) ? null : ca.getElementsByTagName("SecurityNumber").item(0).getTextContent();			
			//              <PhoneNumber>08 - 111 111 11</PhoneNumber>
			String caPhoneNumber = (ca.getElementsByTagName("PhoneNumber").item(0) == null) ? null : ca.getElementsByTagName("PhoneNumber").item(0).getTextContent();
			//              <AddressLine1>c/o Eriksson, Erik</AddressLine1>
			String caAddressLine1 = (ca.getElementsByTagName("AddressLine1").item(0) == null) ? null : ca.getElementsByTagName("AddressLine1").item(0).getTextContent();			
			//              <AddressLine2>Testgatan 1</AddressLine2>
			String caAddressLine2 = (ca.getElementsByTagName("AddressLine2").item(0) == null) ? null : ca.getElementsByTagName("AddressLine2").item(0).getTextContent();			
			//              <Postcode>99999</Postcode>
			String caPostcode = (ca.getElementsByTagName("Postcode").item(0) == null) ? null : ca.getElementsByTagName("Postcode").item(0).getTextContent();			
			//              <Postarea>Stan</Postarea>
			String caPostarea = (ca.getElementsByTagName("Postarea").item(0) == null) ? null : ca.getElementsByTagName("Postarea").item(0).getTextContent();			
			//              <BusinessType>Business</BusinessType>
			String caBusinessType = (ca.getElementsByTagName("BusinessType").item(0) == null) ? null : ca.getElementsByTagName("BusinessType").item(0).getTextContent();
			//              <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
			String caAddressSelector = (ca.getElementsByTagName("AddressSelector").item(0) == null) ? null : ca.getElementsByTagName("AddressSelector").item(0).getTextContent();
			//              <FirstName>Tess T</FirstName>
			String caFirstName = (ca.getElementsByTagName("FirstName").item(0) == null) ? null : ca.getElementsByTagName("FirstName").item(0).getTextContent();
			//              <LastName>Persson</LastName>
			String caLastName = (ca.getElementsByTagName("LastName").item(0) == null) ? null : ca.getElementsByTagName("LastName").item(0).getTextContent();
			//           </CustomerAddress>

			if( caBusinessType.equals("Business") ) {
				
				CompanyCustomer companyCustomer = new CompanyCustomer()
					//CustomerIdentity
					.setStreetAddress(caAddressLine2, null )
					.setCoAddress( caAddressLine1 )
					.setZipCode(caPostcode)
					.setLocality(caPostarea)
					.setPhoneNumber(caPhoneNumber)
					.setIpAddress( null )
					.setEmail( null )
					//CompanyCustomer
					.setCompanyName(caLegalName)
					.setNationalIdNumber( caSecurityNumber )
					.setVatNumber( null )
					.setAddressSelector( caAddressSelector )
				;
				
				this.customerAddresses.add( companyCustomer );
			}
			
			if( caBusinessType.equals("Person") ) {
				IndividualCustomer individualCustomer = new IndividualCustomer()
					//CustomerIdentity
					.setStreetAddress(caAddressLine2, null )
					.setCoAddress( caAddressLine1 )
					.setZipCode(caPostcode)
					.setLocality(caPostarea)
					.setPhoneNumber(caPhoneNumber)
					.setIpAddress( null )
					.setEmail( null )
					//IndividualCustomer
					.setNationalIdNumber(caSecurityNumber)
					.setBirthDate( null )
					.setName(caFirstName, caLastName)
					.setInitials( null )
				;
				
				this.customerAddresses.add( individualCustomer );
			}
		}			
    }        
}