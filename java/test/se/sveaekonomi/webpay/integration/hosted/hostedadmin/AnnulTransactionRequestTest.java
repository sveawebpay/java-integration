package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Hashtable;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;

public class AnnulTransactionRequestTest extends TestCase {

	private CancelOrderBuilder order;
	private AnnulTransactionRequest request;
	
	@Before
	public void setUp() {
		order = new CancelOrderBuilder(SveaConfig.getDefaultConfig());
		order.setCountryCode(COUNTRYCODE.SE);
    	order.setTransactionId( "123456" );    	
		request = new AnnulTransactionRequest(SveaConfig.getDefaultConfig())
			.setCountryCode(order.getCountryCode())
			.setTransactionId( Long.toString(order.getOrderId()) );
	}
	
    @Test
    public void test_AnnulTransactionRequest_class_exists() {    	   	        
        assertThat( request, instanceOf(AnnulTransactionRequest.class) );
        assertThat( request, instanceOf(HostedAdminRequest.class) );
    }    
    
    @Test 
    public void test_getRequestMessageXml() {    	
    	
    	String expectedXmlMessage = 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"Java Integration Package\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"2.0.2\"}-->" +
			"<annul>" +
				"<transactionid>123456</transactionid>" +
			"</annul>"
		;
    	
    	assertEquals( expectedXmlMessage, request.getRequestMessageXml( SveaConfig.getDefaultConfig() ) );    
    }
    
    @Test
    public void test_prepareRequest() {

		String merchantId = this.order.getConfig().getMerchantId(PAYMENTTYPE.HOSTED, request.getCountryCode());
    	
    	String expectedXmlMessageBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48IS0teyJYLVN2ZWEtSW50ZWdyYXRpb24tVmVyc2lvbiI6IkludGVncmF0aW9uIHBhY2thZ2UgZGVmYXVsdCBTdmVhVGVzdENvbmZpZ3VyYXRpb25Qcm92aWRlci4iLCJYLVN2ZWEtSW50ZWdyYXRpb24tUGxhdGZvcm0iOiJJbnRlZ3JhdGlvbiBwYWNrYWdlIGRlZmF1bHQgU3ZlYVRlc3RDb25maWd1cmF0aW9uUHJvdmlkZXIuIiwiWC1TdmVhLUxpYnJhcnktTmFtZSI6IkphdmEgSW50ZWdyYXRpb24gUGFja2FnZSIsIlgtU3ZlYS1JbnRlZ3JhdGlvbi1Db21wYW55IjoiSW50ZWdyYXRpb24gcGFja2FnZSBkZWZhdWx0IFN2ZWFUZXN0Q29uZmlndXJhdGlvblByb3ZpZGVyLiIsIlgtU3ZlYS1MaWJyYXJ5LVZlcnNpb24iOiIyLjAuMiJ9LS0+PGFubnVsPjx0cmFuc2FjdGlvbmlkPjEyMzQ1NjwvdHJhbnNhY3Rpb25pZD48L2FubnVsPg==";
    	String expectedMacSha512 = "1b28e80b345caa2c13db65c3eeae9bc0f2473d5f657a7cda376b4362191ecfa58a248622b6b809a5e3b246cdce8f33fff46e524d20ffdc563e79ddf140a3ced5";    	
    	
    	Hashtable<String, String> requestFields = this.request.prepareRequest();
    	assertEquals( expectedXmlMessageBase64, requestFields.get("message") );
    	assertEquals( expectedMacSha512, requestFields.get("mac") );
    	assertEquals( merchantId, requestFields.get("merchantid") );
    }    
}
