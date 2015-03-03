package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.ConfigurationProviderTestData;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CustomerIdentityResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class LegacyTypeCompatibilityUnitTest {
	
	//// legacy WebPay entrypoints	
  	/// legacy (<2.0) CreateOrderBuilder.setXX() type compatibility -- note that getters are not backwards compatible.  	    
    // v1.4 tests: public class CreateInvoiceOrderTest {
	@Test
	public void testInvoiceForIndividualFromSE() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertEquals("Invoice", response.orderType);
		assertTrue(response.isOrderAccepted());
		assertTrue(response.sveaWillBuyOrder);
		assertEquals(250.00, response.amount, 0);

		// CustomerIdentity
		assertEquals("Individual", response.customerIdentity.getCustomerType());
		assertEquals("194605092222", response.customerIdentity.getNationalIdNumber());
		assertEquals("Persson, Tess T", response.customerIdentity.getFullName());
		assertEquals("Testgatan 1", response.customerIdentity.getStreet());
		assertEquals("c/o Eriksson, Erik", response.customerIdentity.getCoAddress());
		assertEquals("99999", response.customerIdentity.getZipCode());
		assertEquals("Stan", response.customerIdentity.getCity());
		assertEquals("SE", response.customerIdentity.getCountryCode());
	}
	@Test
	public void testInvoiceRequestFailing() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addCustomerDetails(Item.individualCustomer().setNationalIdNumber("")).setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate)
				.setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertFalse(response.isOrderAccepted());
	}
	@Test
	public void testFormationOfDecimalsInCalculation() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
				.addOrderRow(Item.orderRow().setArticleNumber("1").setQuantity(2.0).setAmountExVat(22.68).setDescription("Specification").setName("Prod").setVatPercent(6.0).setDiscountPercent(0.0))
				.addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
		assertEquals(48.08, response.amount, 0);
	}
	@Test
	public void testInvoiceCompanySe() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate)
				.setCurrency(TestingTool.DefaultTestCurrency).addCustomerDetails(TestingTool.createMiniCompanyCustomer()).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).useInvoicePayment()
				.doRequest();

		assertTrue(response.isOrderAccepted());
		assertTrue(response.sveaWillBuyOrder);
		assertEquals("SE", response.customerIdentity.getCountryCode());
	}
	@Test
	public void testInvoiceForIndividualFromNl() {
		CreateOrderResponse response = WebPay
				.createOrder(SveaConfig.getDefaultConfig())
				.addOrderRow(TestingTool.createOrderRowNl())
				.addCustomerDetails(
						Item.individualCustomer().setBirthDate(1955, 03, 07).setInitials("SB").setName("Sneider", "Boasman").setStreetAddress("Gate", "42").setLocality("BARENDRECHT")
								.setZipCode("1102 HG").setCoAddress("138")).setCountryCode(COUNTRYCODE.NL).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
				.setOrderDate(TestingTool.DefaultTestDate).setCurrency(CURRENCY.EUR).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
		assertTrue(response.sveaWillBuyOrder);
		assertEquals(212.00, response.amount, 0);
		assertEquals("0", response.getResultCode());
		assertEquals("Invoice", response.orderType);

		// CustomerIdentity
		assertEquals("Individual", response.customerIdentity.getCustomerType());
		assertEquals("Sneider Boasman", response.customerIdentity.getFullName());
		assertNull(response.customerIdentity.getPhoneNumber());
		assertNull(response.customerIdentity.getEmail());
		assertNull(response.customerIdentity.getIpAddress());
		assertEquals("Gate 42", response.customerIdentity.getStreet());
		assertEquals("138", response.customerIdentity.getCoAddress());
		assertEquals("23", response.customerIdentity.getHouseNumber());
		assertEquals("1102 HG", response.customerIdentity.getZipCode());
		assertEquals("BARENDRECHT", response.customerIdentity.getCity());
		assertEquals("NL", response.customerIdentity.getCountryCode());
	}
	@Test
	public void testInvoiceDoRequestWithIpAddressSetSE() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
				.addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber).setIpAddress("123.123.123"))
				.setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
				.setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
	}
	@Test
	public void testInvoiceRequestUsingAmountIncVatWithZeroVatPercent() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
				.addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency)
				.setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
	}
	@Test
	public void testFailOnMissingCountryCodeOfCloseOrder() {
		Long orderId = 0L;
		SveaSoapBuilder soapBuilder = new SveaSoapBuilder();

		SveaRequest<SveaCreateOrder> request = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setOrderDate(TestingTool.DefaultTestDate).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment()
				.prepareRequest();

		WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();

		String xml = xmlBuilder.getCreateOrderEuXml(request.request);
		String url = SveaConfig.getTestWebserviceUrl().toString();
		String soapMessage = soapBuilder.makeSoapMessage("CreateOrderEu", xml);
		NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, url);
		CreateOrderResponse response = new CreateOrderResponse(soapResponse);
		orderId = response.orderId;

		assertTrue(response.isOrderAccepted());

		CloseOrder closeRequest = WebPay.closeOrder(SveaConfig.getDefaultConfig()).setOrderId(orderId).closeInvoiceOrder();

		String expectedMsg = "MISSING VALUE - CountryCode is required, use setCountryCode(...).\n";

		assertEquals(expectedMsg, closeRequest.validateRequest());
	}
	@Test
	public void testConfiguration() {
		ConfigurationProviderTestData conf = new ConfigurationProviderTestData();
		CreateOrderResponse response = WebPay.createOrder(conf).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).addOrderRow(TestingTool.createExVatBasedOrderRow("2"))
				.addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber).setIpAddress("123.123.123"))
				.setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
				.setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
	}
	@Test
	public void testFormatShippingFeeRowsZero() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addFee(Item.shippingFee().setShippingId("0").setName("Tess").setDescription("Tester").setAmountExVat(0).setVatPercent(0).setUnit("st"))
				.addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setOrderDate(TestingTool.DefaultTestDate).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setCurrency(TestingTool.DefaultTestCurrency)
				.setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber).useInvoicePayment().doRequest();

		assertTrue(response.isOrderAccepted());
	}
	@Test
	public void testCompanyIdResponse() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
				.addCustomerDetails(Item.companyCustomer().setNationalIdNumber(TestingTool.DefaultTestCompanyNationalIdNumber)).setCountryCode(TestingTool.DefaultTestCountryCode)
				.setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setOrderDate(TestingTool.DefaultTestDate).setCurrency(TestingTool.DefaultTestCurrency).useInvoicePayment().doRequest();

		assertFalse(response.isIndividualIdentity);
		assertTrue(response.isOrderAccepted());
	}
	@Test
	public void testInvoiceCompanyDe() {
		CreateOrderResponse response = WebPay
				.createOrder(SveaConfig.getDefaultConfig())
				.addOrderRow(TestingTool.createOrderRowDe())
				.addCustomerDetails(Item.companyCustomer().setNationalIdNumber("12345").setVatNumber("DE123456789").setStreetAddress("Adalbertsteinweg", "1").setZipCode("52070").setLocality("AACHEN"))
				.setCountryCode(COUNTRYCODE.DE).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setOrderDate(TestingTool.DefaultTestDate).setCurrency(CURRENCY.EUR).useInvoicePayment()
				.doRequest();

		assertFalse(response.isIndividualIdentity);
		assertTrue(response.isOrderAccepted());
	}
	@Test
	public void testInvoiceCompanyNl() {
		CreateOrderResponse response = WebPay
				.createOrder(SveaConfig.getDefaultConfig())
				.addOrderRow(TestingTool.createOrderRowNl())
				.addCustomerDetails(
						Item.companyCustomer().setCompanyName("Svea bakkerij 123").setVatNumber("NL123456789A12").setStreetAddress("broodstraat", "1").setZipCode("1111 CD").setLocality("BARENDRECHT"))
				.setCountryCode(COUNTRYCODE.NL).setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber).setOrderDate(TestingTool.DefaultTestDate).setCurrency(CURRENCY.EUR).useInvoicePayment()
				.doRequest();

		assertFalse(response.isIndividualIdentity);
		assertTrue(response.isOrderAccepted());
	}
	
  	/// legacy (<2.0) DeliverOrderBuilder.setXX() type compatibility -- note that getters are not backwards compatible.  	
    @Test
    public void test_DeliverOrderBuilder_setXX_parameter_types() {
    	
    	DeliverOrderBuilder builder = new DeliverOrderBuilder(SveaConfig.getDefaultConfig());

    	long orderId = 123456L;
    	builder.setOrderId( orderId );
    	String orderType = "Invoice";
    	builder.setOrderType( orderType );
    	DISTRIBUTIONTYPE invoiceDistributionType = DISTRIBUTIONTYPE.Post;
    	builder.setInvoiceDistributionType( invoiceDistributionType );
    	String invoiceId = "987654321";
    	builder.setCreditInvoice( invoiceId );
    	int numberOfCreditDays = 10;
    	builder.setNumberOfCreditDays(numberOfCreditDays);
    	
		
    }
	
    // v1.4 tests: public class DeliverInvoiceOrderTest {    
    @Test
    public void testDeliverInvoiceOrderDoRequest() {
        DeliverOrderResponse response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setOrderId(54086L)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder()
            .doRequest();
        
        response.getErrorMessage();
    }    
    @Test
    public void testDeliverInvoiceOrderResult() {
        long orderId = createInvoiceAndReturnOrderId();
        
        DeliverOrderResponse response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setOrderId(orderId)
            .setNumberOfCreditDays(1)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder()
            .doRequest();
        
        assertTrue(response.isOrderAccepted());
        assertEquals("Post", response.getInvoiceDistributionType());
        assertNotNull(response.getOcr());
        assertTrue(0 < response.getOcr().length());
        assertEquals(0.0, response.getLowestAmountToPay(), 0);
    }
    private long createInvoiceAndReturnOrderId() {
        CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .useInvoicePayment()
            .doRequest();
        
        return response.orderId;
    }
	
    /// legacy (<2.0) CloseOrderBuilder compatibility
    // v1.4 tests: public class CloseOrderTest {        
    @Test
    public void testCloseOrder() {
        Long orderId = 0L;
        SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(Item.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .useInvoicePayment()
                .prepareRequest();
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        
        String xml = xmlBuilder.getCreateOrderEuXml(request.request);
        String url = SveaConfig.getTestWebserviceUrl().toString();
        String soapMessage = soapBuilder.makeSoapMessage("CreateOrderEu", xml);
        NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, url);
        CreateOrderResponse response = new CreateOrderResponse(soapResponse);
        orderId = response.orderId;
        
        assertTrue(response.isOrderAccepted());
        
        CloseOrderResponse closeResponse = WebPay.closeOrder(SveaConfig.getDefaultConfig())
                .setOrderId(orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .closeInvoiceOrder()
                .doRequest();
        
        assertTrue(closeResponse.isOrderAccepted());
    }
    
    /// legacy (<2.0) GetAddresses compatibility
    // v1.4 tests: public class GetAddressesTest {     
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
    	GetAddressesResponse response = WebPay.getAddresses(SveaConfig.getDefaultConfig())
                                                         .setCountryCode(COUNTRYCODE.NO)
                                                         .setOrderTypeInvoice() 
                                                         .setIndividual("17054512066")
                                                         .doRequest();

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
    
    
    
    
	
	/// Legacy (<2.0) CreateOrderResponse attribute access -- public attributes
    // OK
	@SuppressWarnings("unused")
	@Test
	public void test_CreateOrderResponse_attributes_visible_and_having_legacy_return_types() throws SOAPException, IOException {

		String dummyResponseXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><CreateOrderEuResponse xmlns=\"https://webservices.sveaekonomi.se/webpay\"><CreateOrderEuResult><Accepted>true</Accepted><ResultCode>0</ResultCode><CreateOrderResult><SveaOrderId>537628</SveaOrderId><OrderType>Invoice</OrderType><SveaWillBuyOrder>true</SveaWillBuyOrder><Amount>1562.50</Amount><CustomerIdentity><NationalIdNumber>194605092222</NationalIdNumber><FullName>Persson, Tess T</FullName><Street>Testgatan 1</Street><CoAddress>c/o Eriksson, Erik</CoAddress><ZipCode>99999</ZipCode><Locality>Stan</Locality><CountryCode>SE</CountryCode><CustomerType>Individual</CustomerType></CustomerIdentity><ExpirationDate>2015-05-01T00:00:00+02:00</ExpirationDate><ClientOrderNumber>743</ClientOrderNumber></CreateOrderResult></CreateOrderEuResult></CreateOrderEuResponse></soap:Body></soap:Envelope>";
		
	    MessageFactory factory = MessageFactory.newInstance();
	    SOAPMessage dummyMessage = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(dummyResponseXml.getBytes(Charset.forName("UTF-8"))));
		
		CreateOrderResponse response = new CreateOrderResponse(dummyMessage.getSOAPPart().getEnvelope().getElementsByTagName("CreateOrderEuResult"));
		
		// WebServiceResponse
		//private boolean isOrderAccepted;
		assertTrue( response.isOrderAccepted() );	// TODO check exists in 1.4?
		//private String resultCode;
		assertEquals( "0", response.getResultCode() );		// TODO check exists in 1.4?
		//private String errorMessage;
		assertEquals( null, response.getErrorMessage() );		// TODO check exists in 1.4?	    
		// GetOrdersResponse
		//public long orderId;
		long myOrderId = response.orderId;
		//public String orderType;
		String myOrderType = response.orderType;				
		//public boolean sveaWillBuyOrder;
		boolean mySveaWillBuyOrder = response.sveaWillBuyOrder;
		//public double amount;
		double myAmount = response.amount;				
		//public CustomerIdentityResponse customerIdentity;
		CustomerIdentityResponse myCustomerIdentity = response.customerIdentity;
		//public String expirationDate;
		String myExpirationDate = response.expirationDate;				
		//public String clientOrderNumber;
		String myClientOrderNumber = response.clientOrderNumber;				
		//public boolean isIndividualIdentity;
		boolean myIsIndividualIdentity = response.isIndividualIdentity;		
	}

	/// Legacy (<2.0) CustomerIdentityResponse attribute access -- getXX() methods	
	// OK
	@Test
	public void test_CreateOrderResponse_individual_CustomerIdentityResponse_attributes_visible_and_having_legacy_return_types() throws SOAPException, IOException {

		String dummyResponseXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><soap:Body><CreateOrderEuResponse xmlns=\"https://webservices.sveaekonomi.se/webpay\"><CreateOrderEuResult><Accepted>true</Accepted><ResultCode>0</ResultCode><CreateOrderResult><SveaOrderId>537628</SveaOrderId><OrderType>Invoice</OrderType><SveaWillBuyOrder>true</SveaWillBuyOrder><Amount>1562.50</Amount><CustomerIdentity><NationalIdNumber>194605092222</NationalIdNumber><FullName>Persson, Tess T</FullName><Street>Testgatan 1</Street><CoAddress>c/o Eriksson, Erik</CoAddress><ZipCode>99999</ZipCode><Locality>Stan</Locality><CountryCode>SE</CountryCode><CustomerType>Individual</CustomerType></CustomerIdentity><ExpirationDate>2015-05-01T00:00:00+02:00</ExpirationDate><ClientOrderNumber>743</ClientOrderNumber></CreateOrderResult></CreateOrderEuResult></CreateOrderEuResponse></soap:Body></soap:Envelope>";
		
	    MessageFactory factory = MessageFactory.newInstance();
	    SOAPMessage dummyMessage = factory.createMessage(new MimeHeaders(), new ByteArrayInputStream(dummyResponseXml.getBytes(Charset.forName("UTF-8"))));
		
		CreateOrderResponse response = new CreateOrderResponse(dummyMessage.getSOAPPart().getEnvelope().getElementsByTagName("CreateOrderEuResult"));
		
		//public CustomerIdentityResponse customerIdentity;
		CustomerIdentityResponse myCustomerIdentity = response.customerIdentity;
		
		//Customer
		String myNationalIdNumber = myCustomerIdentity.getNationalIdNumber(); 
		assertEquals( "194605092222", myNationalIdNumber); 
		String myFullName        =  myCustomerIdentity.getFullName();      
		assertEquals( "Persson, Tess T", myFullName);
		String myIntitials       =  myCustomerIdentity.getIntitials();        
		assertEquals( null, myIntitials);
		String myCoAddress       =  myCustomerIdentity.getCoAddress();        
		assertEquals( "c/o Eriksson, Erik", myCoAddress);
		String myStreet          =  myCustomerIdentity.getStreet();           
		assertEquals( "Testgatan 1", myStreet);
		String myHouseNumber     =  myCustomerIdentity.getHouseNumber();
		assertEquals( null, myHouseNumber);		
		String myZipCode         =  myCustomerIdentity.getZipCode();          
		assertEquals( "99999", myZipCode);
		String myCity            =  myCustomerIdentity.getCity();             
		assertEquals( "Stan", myCity);
		String myCountryCode     =  myCustomerIdentity.getCountryCode();      
		assertEquals( "SE", myCountryCode);
		String myPhoneNumber     =  myCustomerIdentity.getPhoneNumber();      
		assertEquals( null, myPhoneNumber);
		String myEmail           =  myCustomerIdentity.getEmail();            
		assertEquals( null, myEmail);
		String myCustomerType    =  myCustomerIdentity.getCustomerType();     
		assertEquals( "Individual", myCustomerType);
		String myIpAddress       =  myCustomerIdentity.getIpAddress();    	
		assertEquals( null, myIpAddress);		
	}

	/// Legacy (<2.0) DeliverOrderResponse attribute access
	// TODO

	/// Legacy (<2.0) CloseOrderResponse attribute access
	// TODO
}

