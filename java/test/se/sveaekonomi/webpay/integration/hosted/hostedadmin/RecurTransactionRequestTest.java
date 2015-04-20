package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import junit.framework.TestCase;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.config.SveaTestConfigurationProvider;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodPayment;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.RecurTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class RecurTransactionRequestTest extends TestCase {

	/// xml
	@Test 
    public void test_getRequestMessageXml() {    	
    
		RecurTransactionRequest request = new RecurTransactionRequest( SveaConfig.getDefaultConfig() )
			.setCustomerRefNo( "123456" )
			.setSubscriptionId( "9999999" )
			.setCurrency("SEK")
			.setAmount("10000")					
		;			
		
    	String expectedXmlMessage = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}-->" +
			"<recur>" +
				"<amount>10000</amount>" +				
				"<customerrefno>123456</customerrefno>" +
				"<subscriptionid>9999999</subscriptionid>" +
				"<currency>SEK</currency>" +
			"</recur>"
		;  	
    	    	
    	assertEquals( expectedXmlMessage, request.getRequestMessageXml(SveaConfig.getDefaultConfig()) );    
    }

	/// validation
	
    @Test
    public void test_validates_all_required_methods_for_RecurTransactionRequest_doRequest() {

		CreateOrderBuilder builder = WebPay.createOrder(SveaConfig.getDefaultConfig())
	            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
	            .addCustomerDetails(TestingTool.createIndividualCustomer(TestingTool.DefaultTestCountryCode))
	            .setCountryCode(TestingTool.DefaultTestCountryCode)
	            .setClientOrderNumber(Long.toString((new java.util.Date()).getTime()))
	            .setCurrency(TestingTool.DefaultTestCurrency)
	    ;
		
		PaymentMethodPayment request = (PaymentMethodPayment) builder.usePaymentMethod(PAYMENTMETHOD.KORTCERT)			
			.setSubscriptionId("9999999" )
			.setReturnUrl("foo.bar")
		;

		try {
			RecurTransactionResponse response = request.doRecur();				
		}
		catch (Exception e){			
			// fail on validation error			
			fail("Unexpected Exception.");
		}	 
    }    
	
	@Test
	public void test_validates_missing_setCountryCode() {

		CreateOrderBuilder builder = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(TestingTool.createIndividualCustomer(TestingTool.DefaultTestCountryCode))
            //.setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(Long.toString((new java.util.Date()).getTime()))
            .setCurrency(TestingTool.DefaultTestCurrency)
	    ;
	
		PaymentMethodPayment request = (PaymentMethodPayment) builder.usePaymentMethod(PAYMENTMETHOD.KORTCERT)			
			.setSubscriptionId("9999999" )
			.setReturnUrl("foo.bar")
		;
		
		try {
			RecurTransactionResponse response = request.doRecur();				
			// fail if validation passes
	        fail("Missing expected exception");	
        }
		catch (Exception e){			
			assertEquals( 
        		"MISSING VALUE - CountryCode is required. Use setCountryCode(...).\n",
    			e.getCause().getMessage()
    		);
        }	
	}		

	@Test
	public void test_validates_missing_required_setSubscriptionId_for_doRecur() {

		CreateOrderBuilder builder = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(TestingTool.createIndividualCustomer(TestingTool.DefaultTestCountryCode))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(Long.toString((new java.util.Date()).getTime()))
            .setCurrency(TestingTool.DefaultTestCurrency)
	    ;
	
		PaymentMethodPayment request = (PaymentMethodPayment) builder.usePaymentMethod(PAYMENTMETHOD.KORTCERT)			
			//.setSubscriptionId("9999999" )
			.setReturnUrl("foo.bar")
		;
		
		try {
			RecurTransactionResponse response = request.doRecur();				
			// fail if validation passes
	        fail("Missing expected exception");	
        }
		catch (Exception e){			
			assertEquals( 
        		"MISSING VALUE - subscriptionId is required. Use function setSubscriptionId() with the subscriptionId from the createOrder response.\n",
    			e.getCause().getMessage()
    		);
        }	
	}

	@Test
	public void test_validates_missing_required_setReturnUrl_for_doRecur() {

		CreateOrderBuilder builder = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(TestingTool.createIndividualCustomer(TestingTool.DefaultTestCountryCode))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(Long.toString((new java.util.Date()).getTime()))
            .setCurrency(TestingTool.DefaultTestCurrency)
	    ;
	
		PaymentMethodPayment request = (PaymentMethodPayment) builder.usePaymentMethod(PAYMENTMETHOD.KORTCERT)			
			.setSubscriptionId("9999999" )
			//.setReturnUrl("foo.bar")
		;
		
		try {
			RecurTransactionResponse response = request.doRecur();				
			// fail if validation passes
	        fail("Missing expected exception");	
        }
		catch (Exception e){			
			assertEquals( 
        		"MISSING VALUE - Return url is required, setReturnUrl(...).\n",
    			e.getCause().getMessage()
    		);
        }	
	}	
}
