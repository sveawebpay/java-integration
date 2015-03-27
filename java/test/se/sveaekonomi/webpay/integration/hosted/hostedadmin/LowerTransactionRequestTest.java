package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import java.util.Hashtable;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.security.HashUtil;
import se.sveaekonomi.webpay.integration.util.security.HashUtil.HASHALGORITHM;

public class LowerTransactionRequestTest extends TestCase {

	private LowerTransactionRequest request;
	
	@Before
	public void setUp() {
    	request = new LowerTransactionRequest(SveaConfig.getDefaultConfig()); 
    	request.setCountryCode(COUNTRYCODE.SE);
    	request.setTransactionId( "123456" );
    	request.setAmountToLower(100);
	}
	
    @Test
    public void test_LowerTransactionRequest_class_exists() {    	   	        
        assertThat( request, instanceOf(LowerTransactionRequest.class) );
        assertThat( request, instanceOf(HostedAdminRequest.class) );
    }    
    
    @Test 
    public void test_getRequestMessageXml() {    	
    	
		// uncomment to get expectedXmlMessage
		//String secretWord = this.getConfig().getSecretWord(PAYMENTTYPE.HOSTED, request.getCountryCode()); // uncomment to get below xml  	 
		//String expectedXmlMessage = request.getRequestMessageXml(); // uncomment to get below xml
		//System.out.println(expectedXmlMessage); // uncomment to get below xml
	
    	String expectedXmlMessage = 
    			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    			"<!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"2.0.2\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"Java Integration Package\"}-->" +
    			"<loweramount>" +
    				"<transactionid>123456</transactionid>" +
    				"<amounttolower>100</amounttolower>" +
				"</loweramount>"
		;
    	
    	assertEquals( expectedXmlMessage, request.getRequestMessageXml(SveaConfig.getDefaultConfig()) );    
    }
    
    @Test
    public void test_prepareRequest() {

		String merchantId = SveaConfig.getDefaultConfig().getMerchantId(PAYMENTTYPE.HOSTED, request.getCountryCode());

		// uncomment to get expectedXmlMessageBase64
//		String secretWord = SveaConfig.getDefaultConfig().getSecretWord(PAYMENTTYPE.HOSTED, request.getCountryCode());   	     	
//		String expectedXmlMessage = request.getRequestMessageXml();
//		String expectedXmlMessageBase64 = Base64Util.encodeBase64String(expectedXmlMessage);
//		System.out.println(expectedXmlMessageBase64);
    	String expectedXmlMessageBase64 = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48IS0teyJYLVN2ZWEtSW50ZWdyYXRpb24tVmVyc2lvbiI6IkludGVncmF0aW9uIHBhY2thZ2UgZGVmYXVsdCBTdmVhVGVzdENvbmZpZ3VyYXRpb25Qcm92aWRlci4iLCJYLVN2ZWEtSW50ZWdyYXRpb24tUGxhdGZvcm0iOiJJbnRlZ3JhdGlvbiBwYWNrYWdlIGRlZmF1bHQgU3ZlYVRlc3RDb25maWd1cmF0aW9uUHJvdmlkZXIuIiwiWC1TdmVhLUxpYnJhcnktTmFtZSI6IjIuMC4yIiwiWC1TdmVhLUludGVncmF0aW9uLUNvbXBhbnkiOiJJbnRlZ3JhdGlvbiBwYWNrYWdlIGRlZmF1bHQgU3ZlYVRlc3RDb25maWd1cmF0aW9uUHJvdmlkZXIuIiwiWC1TdmVhLUxpYnJhcnktVmVyc2lvbiI6IkphdmEgSW50ZWdyYXRpb24gUGFja2FnZSJ9LS0+PGxvd2VyYW1vdW50Pjx0cmFuc2FjdGlvbmlkPjEyMzQ1NjwvdHJhbnNhY3Rpb25pZD48YW1vdW50dG9sb3dlcj4xMDA8L2Ftb3VudHRvbG93ZXI+PC9sb3dlcmFtb3VudD4=";
    	
//    	String expectedMacSha512 =  HashUtil.createHash(expectedXmlMessageBase64 + secretWord, HASHALGORITHM.SHA_512); // uncomment to get below xml  
//		System.out.println(expectedMacSha512); // uncomment to get below xml    	
    	String expectedMacSha512 = "1349d956d0d78b7c342be5c65c1753b004845e129cdf5d283aec43f27f4d08fdb0fab0b13e0e75a210df9942763b7505bebb967766a192c042c82c0cc55d9c4e";    	
    	
    	Hashtable<String, String> requestFields = this.request.prepareRequest();
    	assertEquals( expectedXmlMessageBase64, requestFields.get("message") );
    	assertEquals( expectedMacSha512, requestFields.get("mac") );
    	assertEquals( merchantId, requestFields.get("merchantid") );
    }
    

}
