package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.HostedAdminResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.LowerTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class LowerTransactionRequestIntegrationTest extends TestCase {

	private DeliverOrderRowsBuilder order;
	private LowerTransactionRequest request;
	
	@Before
	public void setUp() {
		order = new DeliverOrderRowsBuilder(SveaConfig.getDefaultConfig());
    	order.setCountryCode(COUNTRYCODE.SE);

    	request = new LowerTransactionRequest(SveaConfig.getDefaultConfig()); 
    	request.setCountryCode(COUNTRYCODE.SE);
    	request.setTransactionId( "123456" );
    	request.setAmountToLower(100);
	}
	
    @Test
    public void test_doRequest_returns_LowerTransactionResponse_failure() {
    	
    	LowerTransactionResponse response = (LowerTransactionResponse) this.request.doRequest();

    	assertThat( response, instanceOf(LowerTransactionResponse.class) );
        assertThat( response, instanceOf(HostedAdminResponse.class) );
          
        // if we receive an error from the service, the integration test passes
        assertFalse( response.isOrderAccepted() );
    	assertEquals( response.getResultCode(), "128 (NO_SUCH_TRANS)" );      	
    }
    
//    @Test
//    public void manual_test_doRequest_returns_AnnulTransactionResponse_success() {	// TODO replace with webdriver test
//    	this.request.setTransactionId( "584534" );
//    	
//    	LowerTransactionResponse response = this.request.doRequest();
//
//    	assertThat( response, instanceOf(LowerTransactionResponse.class) );
//        assertThat( response, instanceOf(HostedAdminResponse.class) );
//          
//        // if we receive an error from the service, the integration test passes
//        assertTrue( response.isOrderAccepted() );
//        assertEquals( response.getTransactionId(), "584534" );
//    	assertEquals( response.getClientOrderNumber(), "test_1405694000814" );      	
//	}        
    
}
