package se.sveaekonomi.webpay.integration;

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
import se.sveaekonomi.webpay.integration.response.hosted.SveaResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CustomerIdentityResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class LegacyTypeCompatibilityIntegrationTest {
	
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
	public void testInvoiceCompanySe() {
		CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig()).setCountryCode(TestingTool.DefaultTestCountryCode).setOrderDate(TestingTool.DefaultTestDate)
				.setCurrency(TestingTool.DefaultTestCurrency).addCustomerDetails(TestingTool.createMiniCompanyCustomer()).addOrderRow(TestingTool.createExVatBasedOrderRow("1")).useInvoicePayment()
				.doRequest();

		assertTrue(response.isOrderAccepted());
		assertTrue(response.sveaWillBuyOrder);
		assertEquals("SE", response.customerIdentity.getCountryCode());
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
	
	/// Legacy (<2.0) SveaResponse (HostedPaymentResponse) attribute access -- getXX() methods	
	@Test
	public void test_SveaResponse_attributes_visible_and_having_legacy_return_types() {

    	// sample order using default test merchant 1130 credentials
    	String responseXmlBase64 = "PD94bWwgdmVyc2lvbj0nMS4wJyBlbmNvZGluZz0nVVRGLTgnPz48cmVzcG9uc2U+PHRyYW5zYWN0aW9uIGlkPSI1OTU1NDQiPjxwYXltZW50bWV0aG9kPktPUlRDRVJUPC9wYXltZW50bWV0aG9kPjxtZXJjaGFudGlkPjExMzA8L21lcmNoYW50aWQ+PGN1c3RvbWVycmVmbm8+dGVzdF9jcmVhdGVDYXJkT3JkZXIxNDI1Mzc5OTU2ODEzPC9jdXN0b21lcnJlZm5vPjxhbW91bnQ+MjUwMDA8L2Ftb3VudD48Y3VycmVuY3k+U0VLPC9jdXJyZW5jeT48Y2FyZHR5cGU+VklTQTwvY2FyZHR5cGU+PG1hc2tlZGNhcmRubz40NDQ0MzN4eHh4eHgxMTAwPC9tYXNrZWRjYXJkbm8+PGV4cGlyeW1vbnRoPjAxPC9leHBpcnltb250aD48ZXhwaXJ5eWVhcj4xNzwvZXhwaXJ5eWVhcj48YXV0aGNvZGU+Mjg0NjMxPC9hdXRoY29kZT48Y3VzdG9tZXI+PGZpcnN0bmFtZT5UZXNzPC9maXJzdG5hbWU+PGxhc3RuYW1lPlBlcnNzb248L2xhc3RuYW1lPjxpbml0aWFscz5TQjwvaW5pdGlhbHM+PGVtYWlsPnRlc3RAc3ZlYS5jb208L2VtYWlsPjxzc24+MTk0NjA1MDkyMjIyPC9zc24+PGFkZHJlc3M+VGVzdGdhdGFuPC9hZGRyZXNzPjxhZGRyZXNzMj5jL28gRXJpa3Nzb24sIEVyaWs8L2FkZHJlc3MyPjxjaXR5PlN0YW48L2NpdHk+PGNvdW50cnk+U0U8L2NvdW50cnk+PHppcD45OTk5OTwvemlwPjxwaG9uZT4wODExMTExMTExPC9waG9uZT48dmF0bnVtYmVyLz48aG91c2VudW1iZXI+MTwvaG91c2VudW1iZXI+PGNvbXBhbnluYW1lLz48ZnVsbG5hbWUvPjwvY3VzdG9tZXI+PC90cmFuc2FjdGlvbj48c3RhdHVzY29kZT4wPC9zdGF0dXNjb2RlPjwvcmVzcG9uc2U+";
    	String secretWord = "8a9cece566e808da63c6f07ff415ff9e127909d000d259aba24daa2fed6d9e3f8b0b62e8ad1fa91c7d7cd6fc3352deaae66cdb533123edf127ad7d1f4c77e7a3"; 

    	SveaResponse response = new SveaResponse(responseXmlBase64, secretWord);

		String transactionId = response.getTransactionId();
	    String clientOrderNumber = response.getClientOrderNumber();
		String paymentMethod = response.getPaymentMethod();
		String merchantId = response.getMerchantId();
		double amount = response.getAmount();    
		String currency = response.getCurrency();
		String subscriptionId = response.getSubscriptionId();
		String subscriptionType = response.getSubscriptionType();	// removed from 2.0, as never returned by HostedService
		String cardType = response.getCardType();
		String maskedCardNumber = response.getMaskedCardNumber();
		String expiryMonth = response.getExpiryMonth();	
		String expiryYear = response.getExpiryYear();
		String authCode = response.getAuthCode();
    }

    /// Legacy (<2.0) DeliverOrderResponse attribute access -- getXX() methods	
    @Test
    public void test_DeliverOrderResponse_getters_having_legacy_return_types() {
        DeliverOrderResponse response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .setOrderId(54086L)
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .deliverInvoiceOrder()
            .doRequest();
        
        response.getErrorMessage();
        
        ORDERTYPE orderType = response.getOrderType();    	
        double amount = response.getAmount();
        int invoiceId = response.getInvoiceId();
        String dueDate = response.getDueDate();
        String invoiceDate = response.getInvoiceDate();
        String getInvoiceDistributionType = response.getInvoiceDistributionType();		// returned as DISTRIBUTIONTYPE in 2.0
        String ocr = response.getOcr();
        double lowestAmountToPay = response.getLowestAmountToPay();
        int contractNumber = response.getContractNumber();
    }
    
    
}

