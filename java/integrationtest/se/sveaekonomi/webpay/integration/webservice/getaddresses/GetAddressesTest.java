package se.sveaekonomi.webpay.integration.webservice.getaddresses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class GetAddressesTest {
    
	// tests for deprecated (1.6.1) getters in GetOrdersResponse 
    @Test
    public void testGetAddresses() {
        GetAddressesResponse response = WebPay.getAddresses(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setIndividual("460509-2222")
            .setOrderTypeInvoice()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("Persson, Tess T", response.getLegalName());
    }
    
    @Test
    public void testGetAddressesWithoutOrderType() {
        GetAddressesResponse response = WebPay.getAddresses(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setIndividual("460509-2222")
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("Persson, Tess T", response.getLegalName());
    }
    
    @Test
    public void testResultGetAddresses() {
        GetAddressesResponse response = WebPay.getAddresses(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setIndividual(TestingTool.DefaultTestIndividualNationalIdNumber)
            .setOrderTypeInvoice()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("Tess T", response.getFirstName());
        assertEquals("Persson", response.getLastName());
        assertEquals("Testgatan 1", response.getAddressLine2());
        assertEquals("99999", response.getPostcode());
        assertEquals("Stan", response.getPostarea());
    }
    
    @Test
    public void testResultGetIndividualAddressesNO()
    {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
                                                         .setCountryCode(COUNTRYCODE.NO)
                                                         .setOrderTypeInvoice() 
                                                         .setIndividual("17054512066")
        ;
    	GetAddressesResponse response = request.doRequest();
    	assertFalse(response.isOrderAccepted());
    	assertEquals("CountryCode: Supported countries are SE, DK", response.getErrorMessage());
    }

    @Test
    public void testResultGetCompanyAddressesNO()
    {
    	GetAddressesResponse response = WebPay.getAddresses(SveaConfig.getDefaultConfig())
                										 .setCountryCode(COUNTRYCODE.NO)
                                                         .setOrderTypeInvoice()
                                                         .setCompany("923313850")
                                                         .doRequest();

        assertTrue(response.isOrderAccepted());
        assertEquals("Test firma AS", response.getLegalName());
        assertEquals("Testveien 1", response.getAddressLine2());
        assertEquals("Oslo", response.getPostarea());
    }
    
    // new tests for hotfix 1.6.1 (intg-578)    
    // legacy request style
    @Test
    public void test_SE_individual_194605092222_returns_single_address_legacy_request_style()
    {
    	GetAddresses builder = WebPay.getAddresses(SveaConfig.getDefaultConfig())
											.setCountryCode(COUNTRYCODE.SE)
											.setOrderTypeInvoice()
											.setIndividual("194605092222")
		;

		//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		//   <soap:Body>
		//      <GetAddressesResponse xmlns="https://webservices.sveaekonomi.se/webpay">
		//         <GetAddressesResult>
		//            <Accepted>true</Accepted>
		//            <RejectionCode>Accepted</RejectionCode>
		//            <Addresses>
		//               <CustomerAddress>
		//                  <LegalName>Persson, Tess T</LegalName>
		//                  <SecurityNumber>4605092222</SecurityNumber>
		//                  <PhoneNumber>08 - 111 111 11</PhoneNumber>
		//                  <AddressLine1>c/o Eriksson, Erik</AddressLine1>
		//                  <AddressLine2>Testgatan 1</AddressLine2>
		//                  <Postcode>99999</Postcode>
		//                  <Postarea>Stan</Postarea>
		//                  <BusinessType>Person</BusinessType>
		//                  <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
		//                  <FirstName>Tess T</FirstName>
		//                  <LastName>Persson</LastName>
		//               </CustomerAddress>
		//            </Addresses>
		//         </GetAddressesResult>
		//      </GetAddressesResponse>
		//   </soap:Body>
		//</soap:Envelope>
    	
    	GetAddressesResponse response = builder.doRequest();
    	
        assertTrue(response.isOrderAccepted());
        assertEquals(1, response.getIndividualCustomers().size());

        IndividualCustomer address = response.getIndividualCustomers().get(0);
        assertEquals("Testgatan 1", address.getStreetAddress() );
        assertEquals("c/o Eriksson, Erik", address.getCoAddress() );
        assertEquals( "99999", address.getZipCode() );
        assertEquals( "Stan", address.getLocality() );
        assertEquals( "08 - 111 111 11", address.getPhoneNumber() );
        assertEquals( "4605092222", address.getNationalIdNumber() );
        assertEquals( "Tess T", address.getFirstName() );
        assertEquals( "Persson", address.getLastName() );        
        // Fields in  IndividualCustomer that are not available in GetAddresses request response
        // from CustomerIdentity
        assertEquals( null, address.getIpAddress() );
        assertEquals( null, address.getEmail() );
        // from IndividualIdentity
        assertEquals( null, address.getHouseNumber() );
        assertEquals( null, address.getInitials() );
        assertEquals( null, address.getBirthDate() );
        // Fields in GetAddresses request response that have no counterpart in IndividualCustomer, FYI:
        // <LegalName>Persson, Tess T</LegalName>
        // <BusinessType>Person</BusinessType>
		// <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
    }

    @Test
    public void test_SE_company_194608142222_returns_all_two_addresses_legacy_request_style()
    {
    	GetAddresses builder = WebPay.getAddresses(SveaConfig.getDefaultConfig())
											.setCountryCode(COUNTRYCODE.SE)
											.setOrderTypeInvoice()
											.setCompany("194608142222")
		;
    
		//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		//   <soap:Body>
		//      <GetAddressesResponse xmlns="https://webservices.sveaekonomi.se/webpay">
		//         <GetAddressesResult>
		//            <Accepted>true</Accepted>
		//            <RejectionCode>Accepted</RejectionCode>
		//            <Addresses>
		//               <CustomerAddress>
		//                  <LegalName>Persson, Tess T</LegalName>
		//                  <SecurityNumber>4608142222</SecurityNumber>
		//                  <PhoneNumber>08 - 111 111 11</PhoneNumber>
		//                  <AddressLine1>c/o Eriksson, Erik</AddressLine1>
		//                  <AddressLine2>Testgatan 1</AddressLine2>
		//                  <Postcode>99999</Postcode>
		//                  <Postarea>Stan</Postarea>
		//                  <BusinessType>Business</BusinessType>
		//                  <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
		//                  <FirstName>Tess T</FirstName>
		//                  <LastName>Persson</LastName>
		//               </CustomerAddress>
		//               <CustomerAddress>
		//                  <LegalName>Persson, Tess T</LegalName>
		//                  <SecurityNumber>4608142222</SecurityNumber>
		//                  <PhoneNumber>08 - 111 111 11</PhoneNumber>
		//                  <AddressLine1>c/o Eriksson, Erik</AddressLine1>
		//                  <AddressLine2>Testgatan 1, 2</AddressLine2>
		//                  <Postcode>99999</Postcode>
		//                  <Postarea>Stan</Postarea>
		//                  <BusinessType>Business</BusinessType>
		//                  <AddressSelector>F09E3CC5AFB627CACBE22A7BC371DE0047222F7F</AddressSelector>
		//                  <FirstName>Tess T</FirstName>
		//                  <LastName>Persson</LastName>
		//               </CustomerAddress>
		//            </Addresses>
		//         </GetAddressesResult>
		//      </GetAddressesResponse>
		//   </soap:Body>
		//</soap:Envelope>
    	    	
    	GetAddressesResponse response = builder.doRequest();
    	
        assertTrue(response.isOrderAccepted());
        assertEquals(2, response.getCompanyCustomers().size());

        CompanyCustomer address = response.getCompanyCustomers().get(0);
        assertEquals("Testgatan 1", address.getStreetAddress() );
        assertEquals("c/o Eriksson, Erik", address.getCoAddress() );
        assertEquals( "99999", address.getZipCode() );
        assertEquals( "Stan", address.getLocality() );
        assertEquals( "08 - 111 111 11", address.getPhoneNumber() );
        assertEquals( "Persson, Tess T", address.getCompanyName() );
        assertEquals( "4608142222", address.getNationalIdNumber() );
        assertEquals( "5F445B19E8C87954904FB7531A51AEE57C5E9413", address.getAddressSelector() );
        // Fields in  CompanyCustomer that are not available in GetAddresses request response
        // from CustomerIdentity
        assertEquals( null, address.getIpAddress() );
        assertEquals( null, address.getEmail() );
        assertEquals( null, address.getHouseNumber() );
        // from CompanyCustomer
        assertEquals( null, address.getVatNumber() );
        // Fields in GetAddresses request response that have no counterpart in CompanyCustomer, FYI:
        // <BusinessType>Business</BusinessType>
		// <FirstName>Tess T</FirstName>
		// <LastName>Persson</LastName>

        CompanyCustomer address2 = response.getCompanyCustomers().get(1);
        assertEquals("Testgatan 1, 2", address2.getStreetAddress() );
        assertEquals("c/o Eriksson, Erik", address2.getCoAddress() );
        assertEquals( "99999", address2.getZipCode() );
        assertEquals( "Stan", address2.getLocality() );
        assertEquals( "08 - 111 111 11", address2.getPhoneNumber() );
        assertEquals( "Persson, Tess T", address2.getCompanyName() );
        assertEquals( "4608142222", address2.getNationalIdNumber() );
        assertEquals( "F09E3CC5AFB627CACBE22A7BC371DE0047222F7F", address2.getAddressSelector() );
        assertEquals( null, address2.getIpAddress() );
        assertEquals( null, address2.getEmail() );
        assertEquals( null, address.getHouseNumber() );
        assertEquals( null, address2.getVatNumber() );
    }  
    
    // new request style
    @Test
    public void test_SE_individual_194605092222_returns_single_address()
    {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
				.setCountryCode(COUNTRYCODE.SE)
				.setCustomerIdentifier("194605092222")
				.getIndividualAddresses()
		;

		//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		//   <soap:Body>
		//      <GetAddressesResponse xmlns="https://webservices.sveaekonomi.se/webpay">
		//         <GetAddressesResult>
		//            <Accepted>true</Accepted>
		//            <RejectionCode>Accepted</RejectionCode>
		//            <Addresses>
		//               <CustomerAddress>
		//                  <LegalName>Persson, Tess T</LegalName>
		//                  <SecurityNumber>4605092222</SecurityNumber>
		//                  <PhoneNumber>08 - 111 111 11</PhoneNumber>
		//                  <AddressLine1>c/o Eriksson, Erik</AddressLine1>
		//                  <AddressLine2>Testgatan 1</AddressLine2>
		//                  <Postcode>99999</Postcode>
		//                  <Postarea>Stan</Postarea>
		//                  <BusinessType>Person</BusinessType>
		//                  <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
		//                  <FirstName>Tess T</FirstName>
		//                  <LastName>Persson</LastName>
		//               </CustomerAddress>
		//            </Addresses>
		//         </GetAddressesResult>
		//      </GetAddressesResponse>
		//   </soap:Body>
		//</soap:Envelope>
    	
    	GetAddressesResponse response = request.doRequest();
    	
        assertTrue(response.isOrderAccepted());
        assertEquals(1, response.getIndividualCustomers().size());

        IndividualCustomer address = response.getIndividualCustomers().get(0);
        assertEquals("Testgatan 1", address.getStreetAddress() );
        assertEquals("c/o Eriksson, Erik", address.getCoAddress() );
        assertEquals( "99999", address.getZipCode() );
        assertEquals( "Stan", address.getLocality() );
        assertEquals( "08 - 111 111 11", address.getPhoneNumber() );
        assertEquals( "4605092222", address.getNationalIdNumber() );
        assertEquals( "Tess T", address.getFirstName() );
        assertEquals( "Persson", address.getLastName() );        
        // Fields in  IndividualCustomer that are not available in GetAddresses request response
        // from CustomerIdentity
        assertEquals( null, address.getIpAddress() );
        assertEquals( null, address.getEmail() );
        // from IndividualIdentity
        assertEquals( null, address.getHouseNumber() );
        assertEquals( null, address.getInitials() );
        assertEquals( null, address.getBirthDate() );
        // Fields in GetAddresses request response that have no counterpart in IndividualCustomer, FYI:
        // <LegalName>Persson, Tess T</LegalName>
        // <BusinessType>Person</BusinessType>
		// <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
    }

    @Test
    public void test_SE_company_194608142222_returns_all_two_addresses() {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
				.setCountryCode(COUNTRYCODE.SE)
				.setCustomerIdentifier("194608142222")
				.getCompanyAddresses()
		;
    
		//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		//   <soap:Body>
		//      <GetAddressesResponse xmlns="https://webservices.sveaekonomi.se/webpay">
		//         <GetAddressesResult>
		//            <Accepted>true</Accepted>
		//            <RejectionCode>Accepted</RejectionCode>
		//            <Addresses>
		//               <CustomerAddress>
		//                  <LegalName>Persson, Tess T</LegalName>
		//                  <SecurityNumber>4608142222</SecurityNumber>
		//                  <PhoneNumber>08 - 111 111 11</PhoneNumber>
		//                  <AddressLine1>c/o Eriksson, Erik</AddressLine1>
		//                  <AddressLine2>Testgatan 1</AddressLine2>
		//                  <Postcode>99999</Postcode>
		//                  <Postarea>Stan</Postarea>
		//                  <BusinessType>Business</BusinessType>
		//                  <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
		//                  <FirstName>Tess T</FirstName>
		//                  <LastName>Persson</LastName>
		//               </CustomerAddress>
		//               <CustomerAddress>
		//                  <LegalName>Persson, Tess T</LegalName>
		//                  <SecurityNumber>4608142222</SecurityNumber>
		//                  <PhoneNumber>08 - 111 111 11</PhoneNumber>
		//                  <AddressLine1>c/o Eriksson, Erik</AddressLine1>
		//                  <AddressLine2>Testgatan 1, 2</AddressLine2>
		//                  <Postcode>99999</Postcode>
		//                  <Postarea>Stan</Postarea>
		//                  <BusinessType>Business</BusinessType>
		//                  <AddressSelector>F09E3CC5AFB627CACBE22A7BC371DE0047222F7F</AddressSelector>
		//                  <FirstName>Tess T</FirstName>
		//                  <LastName>Persson</LastName>
		//               </CustomerAddress>
		//            </Addresses>
		//         </GetAddressesResult>
		//      </GetAddressesResponse>
		//   </soap:Body>
		//</soap:Envelope>
    	    	
    	GetAddressesResponse response = request.doRequest();
    	
        assertTrue(response.isOrderAccepted());
        assertEquals(2, response.getCompanyCustomers().size());

        CompanyCustomer address = response.getCompanyCustomers().get(0);
        assertEquals("Testgatan 1", address.getStreetAddress() );
        assertEquals("c/o Eriksson, Erik", address.getCoAddress() );
        assertEquals( "99999", address.getZipCode() );
        assertEquals( "Stan", address.getLocality() );
        assertEquals( "08 - 111 111 11", address.getPhoneNumber() );
        assertEquals( "Persson, Tess T", address.getCompanyName() );
        assertEquals( "4608142222", address.getNationalIdNumber() );
        assertEquals( "5F445B19E8C87954904FB7531A51AEE57C5E9413", address.getAddressSelector() );
        // Fields in  CompanyCustomer that are not available in GetAddresses request response
        // from CustomerIdentity
        assertEquals( null, address.getIpAddress() );
        assertEquals( null, address.getEmail() );
        assertEquals( null, address.getHouseNumber() );
        // from CompanyCustomer
        assertEquals( null, address.getVatNumber() );
        // Fields in GetAddresses request response that have no counterpart in CompanyCustomer, FYI:
        // <BusinessType>Business</BusinessType>
		// <FirstName>Tess T</FirstName>
		// <LastName>Persson</LastName>

        CompanyCustomer address2 = response.getCompanyCustomers().get(1);
        assertEquals("Testgatan 1, 2", address2.getStreetAddress() );
        assertEquals("c/o Eriksson, Erik", address2.getCoAddress() );
        assertEquals( "99999", address2.getZipCode() );
        assertEquals( "Stan", address2.getLocality() );
        assertEquals( "08 - 111 111 11", address2.getPhoneNumber() );
        assertEquals( "Persson, Tess T", address2.getCompanyName() );
        assertEquals( "4608142222", address2.getNationalIdNumber() );
        assertEquals( "F09E3CC5AFB627CACBE22A7BC371DE0047222F7F", address2.getAddressSelector() );
        assertEquals( null, address2.getIpAddress() );
        assertEquals( null, address2.getEmail() );
        assertEquals( null, address.getHouseNumber() );
        assertEquals( null, address2.getVatNumber() );
    }  
    
    @Test
    public void test_NO_company_923313850_returns_all_two_addresses()
    {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
				.setCountryCode(COUNTRYCODE.NO)
				.setCustomerIdentifier("923313850")
				.getCompanyAddresses()
		;    	    	
		//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		//    <soap:Body>
		//       <GetAddressesResponse xmlns="https://webservices.sveaekonomi.se/webpay">
		//          <GetAddressesResult>
		//             <Accepted>true</Accepted>
		//             <RejectionCode>Accepted</RejectionCode>
		//             <Addresses>
		//                <CustomerAddress>
		//                   <LegalName>Test firma AS</LegalName>
		//                   <SecurityNumber>923313850</SecurityNumber>
		//                   <PhoneNumber>22222222</PhoneNumber>
		//                   <AddressLine2>Testveien 1</AddressLine2>
		//                   <Postcode>259</Postcode>
		//                   <Postarea>Oslo</Postarea>
		//                   <BusinessType>Business</BusinessType>
		//                   <AddressSelector>D1D75AB9CDF477FD89B5F890E0F2543ECC8FFE5E</AddressSelector>
		//                </CustomerAddress>
		//                <CustomerAddress>
		//                   <LegalName>Test firma AS</LegalName>
		//                   <SecurityNumber>923313850</SecurityNumber>
		//                   <PhoneNumber>22222222</PhoneNumber>
		//                   <AddressLine2>Testveien 1, 2</AddressLine2>
		//                   <Postcode>259</Postcode>
		//                   <Postarea>Oslo</Postarea>
		//                   <BusinessType>Business</BusinessType>
		//                   <AddressSelector>40C7770AB81918320F9FFC180FA7CE2B0854FE5A</AddressSelector>
		//                </CustomerAddress>
		//             </Addresses>
		//          </GetAddressesResult>
		//       </GetAddressesResponse>
		//    </soap:Body>
		// </soap:Envelope>
    
    	GetAddressesResponse response = request.doRequest();
    	
        assertTrue(response.isOrderAccepted());
        assertEquals(2, response.getCompanyCustomers().size());

        CompanyCustomer address = response.getCompanyCustomers().get(0);
        assertEquals("Testveien 1", address.getStreetAddress() );
        assertEquals( null, address.getCoAddress() );
        assertEquals( "259", address.getZipCode() );
        assertEquals( "Oslo", address.getLocality() );
        assertEquals( "22222222", address.getPhoneNumber() );
        assertEquals( "Test firma AS", address.getCompanyName() );
        assertEquals( "923313850", address.getNationalIdNumber() );
        assertEquals( "D1D75AB9CDF477FD89B5F890E0F2543ECC8FFE5E", address.getAddressSelector() );
        // Fields in  CompanyCustomer that are not available in GetAddresses request response
        // from CustomerIdentity
        assertEquals( null, address.getIpAddress() );
        assertEquals( null, address.getEmail() );
        assertEquals( null, address.getHouseNumber() );
        // from CompanyCustomer
        assertEquals( null, address.getVatNumber() );
        // Fields in GetAddresses request response that have no counterpart in CompanyCustomer, FYI:
        // <BusinessType>Business</BusinessType>
		// <FirstName>Tess T</FirstName>
		// <LastName>Persson</LastName>

        CompanyCustomer address2 = response.getCompanyCustomers().get(1);
        assertEquals("Testveien 1, 2", address2.getStreetAddress() );
        assertEquals( null, address2.getCoAddress() );
        assertEquals( "259", address2.getZipCode() );
        assertEquals( "Oslo", address2.getLocality() );
        assertEquals( "22222222", address2.getPhoneNumber() );
        assertEquals( "Test firma AS", address2.getCompanyName() );
        assertEquals( "923313850", address2.getNationalIdNumber() );
        assertEquals( "40C7770AB81918320F9FFC180FA7CE2B0854FE5A", address2.getAddressSelector() );
        assertEquals( null, address2.getIpAddress() );
        assertEquals( null, address2.getEmail() );
        assertEquals( null, address2.getHouseNumber() );
        assertEquals( null, address2.getVatNumber() );
    }      
    
    @Test
    public void test_DK_individual_2603692503_returns_single_address()
    {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
				.setCountryCode(COUNTRYCODE.DK)
				.setCustomerIdentifier("2603692503")
				.getIndividualAddresses()
		;        	
    	
		//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
		//   <soap:Body>
		//      <GetAddressesResponse xmlns="https://webservices.sveaekonomi.se/webpay">
		//         <GetAddressesResult>
		//            <Accepted>true</Accepted>
		//            <RejectionCode>Accepted</RejectionCode>
		//            <Addresses>
		//               <CustomerAddress>
		//                  <LegalName>Jensen Hanne</LegalName>
		//                  <SecurityNumber>2603692503</SecurityNumber>
		//                  <PhoneNumber>22222222</PhoneNumber>
		//                  <AddressLine1>c/o Test A/S</AddressLine1>
		//                  <AddressLine2>Testvejen 42</AddressLine2>
		//                  <Postcode>2100</Postcode>
		//                  <Postarea>KÃ˜BENHVN Ã˜</Postarea>
		//                  <BusinessType>Person</BusinessType>
		//                  <AddressSelector>B3FE1206E74060F7CD784289788DEF391DD1EC31</AddressSelector>
		//                  <FirstName>Hanne</FirstName>
		//                  <LastName>Jensen</LastName>
		//               </CustomerAddress>
		//            </Addresses>
		//         </GetAddressesResult>
		//      </GetAddressesResponse>
		//   </soap:Body>
		//</soap:Envelope>
    	
    	GetAddressesResponse response = request.doRequest();
    	
        assertTrue(response.isOrderAccepted());
        assertEquals(1, response.getIndividualCustomers().size());

        IndividualCustomer address = response.getIndividualCustomers().get(0);
        assertEquals("Testvejen 42", address.getStreetAddress() );
        assertEquals("c/o Test A/S", address.getCoAddress() );
        assertEquals( "2100", address.getZipCode() );
        assertEquals( "KØBENHVN Ø", address.getLocality() );
        assertEquals( "22222222", address.getPhoneNumber() );
        assertEquals( "2603692503", address.getNationalIdNumber() );
        assertEquals( "Hanne", address.getFirstName() );
        assertEquals( "Jensen", address.getLastName() );        
        // Fields in  IndividualCustomer that are not available in GetAddresses request response
        // from CustomerIdentity
        assertEquals( null, address.getIpAddress() );
        assertEquals( null, address.getEmail() );
        // from IndividualIdentity
        assertEquals( null, address.getHouseNumber() );
        assertEquals( null, address.getInitials() );
        assertEquals( null, address.getBirthDate() );
        // Fields in GetAddresses request response that have no counterpart in IndividualCustomer, FYI:
        // <LegalName>Persson, Tess T</LegalName>
        // <BusinessType>Person</BusinessType>
		// <AddressSelector>5F445B19E8C87954904FB7531A51AEE57C5E9413</AddressSelector>
    }
    
    @Test
    public void test_DK_company_99999993_returns_all_two_addresses()
    {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
				.setCountryCode(COUNTRYCODE.DK)
				.setCustomerIdentifier("99999993_")
				.getCompanyAddresses()
		;   
    	
    	//<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
    	//    <soap:Body>
    	//       <GetAddressesResponse xmlns="https://webservices.sveaekonomi.se/webpay">
    	//          <GetAddressesResult>
    	//             <Accepted>true</Accepted>
    	//             <RejectionCode>Accepted</RejectionCode>
    	//             <Addresses>
    	//                <CustomerAddress>
    	//                   <LegalName>Test A/S</LegalName>
    	//                   <SecurityNumber>99999993</SecurityNumber>
    	//                   <PhoneNumber>22222222</PhoneNumber>
    	//                   <AddressLine2>Testvejen 42</AddressLine2>
    	//                   <Postcode>2100</Postcode>
    	//                   <Postarea>KÃ˜BENHVN Ã˜</Postarea>
    	//                   <BusinessType>Business</BusinessType>
    	//                   <AddressSelector>0A331244498B629A383186BF7F21D45D8DFC4C8E</AddressSelector>
    	//                </CustomerAddress>
    	//                <CustomerAddress>
    	//                   <LegalName>Test A/S</LegalName>
    	//                   <SecurityNumber>99999993</SecurityNumber>
    	//                   <PhoneNumber>22222222</PhoneNumber>
    	//                   <AddressLine2>Testvejen 42, 2</AddressLine2>
    	//                   <Postcode>2100</Postcode>
    	//                   <Postarea>KÃ˜BENHVN Ã˜</Postarea>
    	//                   <BusinessType>Business</BusinessType>
    	//                   <AddressSelector>685B3A8251503ED17996D3BFE475450A97149AE8</AddressSelector>
    	//                </CustomerAddress>
    	//             </Addresses>
    	//          </GetAddressesResult>
    	//       </GetAddressesResponse>
    	//    </soap:Body>
    	// </soap:Envelope> 
    
    	GetAddressesResponse response = request.doRequest();
    	
        assertTrue(response.isOrderAccepted());
        assertEquals(2, response.getCompanyCustomers().size());

        CompanyCustomer address = response.getCompanyCustomers().get(0);
        assertEquals("Testvejen 42", address.getStreetAddress() );
        assertEquals( null, address.getCoAddress() );
        assertEquals( "2100", address.getZipCode() );
        assertEquals( "KØBENHVN Ø", address.getLocality() );
        assertEquals( "22222222", address.getPhoneNumber() );
        assertEquals( "Test A/S", address.getCompanyName() );
        assertEquals( "99999993", address.getNationalIdNumber() );
        assertEquals( "0A331244498B629A383186BF7F21D45D8DFC4C8E", address.getAddressSelector() );
        // Fields in  CompanyCustomer that are not available in GetAddresses request response
        // from CustomerIdentity
        assertEquals( null, address.getIpAddress() );
        assertEquals( null, address.getEmail() );
        assertEquals( null, address.getHouseNumber() );
        // from CompanyCustomer
        assertEquals( null, address.getVatNumber() );
        // Fields in GetAddresses request response that have no counterpart in CompanyCustomer, FYI:
        // <BusinessType>Business</BusinessType>
		// <FirstName>Tess T</FirstName>
		// <LastName>Persson</LastName>

        CompanyCustomer address2 = response.getCompanyCustomers().get(1);
        assertEquals("Testvejen 42, 2", address2.getStreetAddress() );
        assertEquals( null, address2.getCoAddress() );
        assertEquals( "2100", address2.getZipCode() );
        assertEquals( "KØBENHVN Ø", address2.getLocality() );
        assertEquals( "22222222", address2.getPhoneNumber() );
        assertEquals( "Test A/S", address2.getCompanyName() );
        assertEquals( "99999993", address2.getNationalIdNumber() );
        assertEquals( "685B3A8251503ED17996D3BFE475450A97149AE8", address2.getAddressSelector() );
        assertEquals( null, address2.getIpAddress() );
        assertEquals( null, address2.getEmail() );
        assertEquals( null, address2.getHouseNumber() );
        assertEquals( null, address2.getVatNumber() );
    }    
 
    // validation
    @Test
    public void testFailOnMissingValuesForGetAddresses() {
        String expectedMessage =
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" +
                "MISSING VALUE - customerIdentifer is required. Use: setCustomerIdentifier().\n"
		;
        try {
            WebPay.getAddresses(SveaConfig.getDefaultConfig())
                .doRequest();
            
            //Fail on no exception
            fail();
        } catch (Exception e) {
            assertEquals(expectedMessage, e.getCause().getMessage());
        }
    }
    
}
