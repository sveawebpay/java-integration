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
			"<!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"2.0.2\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"Java Integration Package\"}-->" +
			"<annul>" +
				"<transactionid>123456</transactionid>" +
			"</annul>"
		;
    	
    	assertEquals( expectedXmlMessage, request.getRequestMessageXml( SveaConfig.getDefaultConfig() ) );    
    }
    
    @Test
    public void test_prepareRequest() {

		String merchantId = this.order.getConfig().getMerchantId(PAYMENTTYPE.HOSTED, request.getCountryCode());
    	
    	String expectedXmlMessageBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48IS0teyJYLVN2ZWEtSW50ZWdyYXRpb24tVmVyc2lvbiI6IkludGVncmF0aW9uIHBhY2thZ2UgZGVmYXVsdCBTdmVhVGVzdENvbmZpZ3VyYXRpb25Qcm92aWRlci4iLCJYLVN2ZWEtSW50ZWdyYXRpb24tUGxhdGZvcm0iOiJJbnRlZ3JhdGlvbiBwYWNrYWdlIGRlZmF1bHQgU3ZlYVRlc3RDb25maWd1cmF0aW9uUHJvdmlkZXIuIiwiWC1TdmVhLUxpYnJhcnktTmFtZSI6IjIuMC4yIiwiWC1TdmVhLUludGVncmF0aW9uLUNvbXBhbnkiOiJJbnRlZ3JhdGlvbiBwYWNrYWdlIGRlZmF1bHQgU3ZlYVRlc3RDb25maWd1cmF0aW9uUHJvdmlkZXIuIiwiWC1TdmVhLUxpYnJhcnktVmVyc2lvbiI6IkphdmEgSW50ZWdyYXRpb24gUGFja2FnZSJ9LS0+PGFubnVsPjx0cmFuc2FjdGlvbmlkPjEyMzQ1NjwvdHJhbnNhY3Rpb25pZD48L2FubnVsPg==";
    	String expectedMacSha512 = "8eacd441483a5e227eba02f8392830100f9a20502f67e96d33f8851b54c051f50c5b48e207c2764ba8ac928e7d010787e76cb06df4cc5fc7bbe737ed0fce08fc";    	
    	
    	Hashtable<String, String> requestFields = this.request.prepareRequest();
    	assertEquals( expectedXmlMessageBase64, requestFields.get("message") );
    	assertEquals( expectedMacSha512, requestFields.get("mac") );
    	assertEquals( merchantId, requestFields.get("merchantid") );
    }    
}
