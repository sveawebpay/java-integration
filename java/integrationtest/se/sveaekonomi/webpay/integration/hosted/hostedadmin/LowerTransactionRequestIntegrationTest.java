package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.AnnulTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.HostedAdminResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.LowerTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class LowerTransactionRequestIntegrationTest extends TestCase {

//	private DeliverOrderRowsBuilder order;
//	private LowerTransactionRequest request;
//	
//	@Before
//	public void setUp() {
//		order = new DeliverOrderRowsBuilder(SveaConfig.getDefaultConfig());
//    	order.setCountryCode(COUNTRYCODE.SE);
//
//    	request = new LowerTransactionRequest(SveaConfig.getDefaultConfig()); 
//    	request.setCountryCode(COUNTRYCODE.SE);
//    	request.setTransactionId( "123456" );
//    	request.setAmountToLower(100);
//	}
	
//    @Test
//    public void test_doRequest_returns_LowerTransactionResponse_failure() {
//    	
//    	LowerTransactionResponse response = (LowerTransactionResponse) this.request.doRequest();
//
//    	assertThat( response, instanceOf(LowerTransactionResponse.class) );
//        assertThat( response, instanceOf(HostedAdminResponse.class) );
//          
//        // if we receive an error from the service, the integration test passes
//        assertFalse( response.isOrderAccepted() );
//    	assertEquals( response.getResultCode(), "128 (NO_SUCH_TRANS)" );      	
//    }

    @Test
    public void test_LowerTransaction() {
    	// create an order using defaults
    	HostedPaymentResponse order = (HostedPaymentResponse)TestingTool.createCardTestOrder("test_cancelOrder_cancelCardOrder");
        assertTrue(order.isOrderAccepted());
        
        // query order
        LowerTransactionRequest request = new LowerTransactionRequest( SveaConfig.getDefaultConfig() )
        	.setTransactionId( order.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
    		.setAmountToLower(100)
        ;                
        LowerTransactionResponse response = request.doRequest();       
        
        assertTrue( response.isOrderAccepted() ); 
		assertEquals( order.getTransactionId(),response.getTransactionId() );	
    }	
	
    
}
