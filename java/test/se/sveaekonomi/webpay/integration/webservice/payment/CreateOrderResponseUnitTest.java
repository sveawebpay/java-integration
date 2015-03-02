package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.ConfigurationProviderTestData;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.CustomerIdentity;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CustomerIdentityResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

public class CreateOrderResponseUnitTest {

	// Legacy (<2.0) CreateOrderResponse attribute access
	
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
}
