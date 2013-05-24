package se.sveaekonomi.webpay.integration.config;

import static org.junit.Assert.*;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class ConfigurationProviderTest {
	
	@Test
	public void testDefaultTestConfig() {
		ConfigurationProvider conf = SveaConfig.getDefaultConfig();
		assertEquals("sverigetest", conf.getUsername(PAYMENTTYPE.INVOICE, COUNTRYCODE.SE));
		assertEquals("sverigetest", conf.getPassword(PAYMENTTYPE.PAYMENTPLAN, COUNTRYCODE.SE));
		assertEquals(36000, conf.getClientNumber(PAYMENTTYPE.PAYMENTPLAN, COUNTRYCODE.NO));
		assertEquals("1130", conf.getMerchantId(PAYMENTTYPE.HOSTED, COUNTRYCODE.NL));
		assertEquals("https://webservices.sveaekonomi.se/webpay_test/SveaWebPay.asmx?WSDL", conf.getEndPoint(PAYMENTTYPE.INVOICE).toString());
	}

	@Test
	public void testConfiguration() throws ValidationException, Exception {
		ConfigurationProviderInterfaceTest conf = new ConfigurationProviderInterfaceTest();
		CreateOrderResponse response = WebPay.createOrder(conf)
	    		.addOrderRow(Item.orderRow()
	                .setArticleNumber(1)
	                .setQuantity(2)
	                .setAmountExVat(100.00)
	                .setDescription("Specification")
	                .setName("Prod")
	                .setUnit("st")
	                .setVatPercent(25)
	                .setDiscountPercent(0))
	                
	             .addOrderRow(Item.orderRow()
	                .setArticleNumber(1)
	                .setQuantity(2)
	                .setAmountExVat(100.00)
	                .setDescription("Specification")
	                .setName("Prod")
	                .setVatPercent(25)
	                .setDiscountPercent(0))
	                                
	             .addCustomerDetails(Item.individualCustomer()
	                 .setNationalIdNumber("194605092222")
	                 .setIpAddress("123.123.123"))                      
	             .setCountryCode(COUNTRYCODE.SE)
	             .setOrderDate("2012-12-12")                
	             .setClientOrderNumber("33")
	             .setCurrency(CURRENCY.SEK)
	             .useInvoicePayment()
	             .doRequest();
	        
	    	assertEquals(response.isOrderAccepted(), true);
	      	
	}
}
