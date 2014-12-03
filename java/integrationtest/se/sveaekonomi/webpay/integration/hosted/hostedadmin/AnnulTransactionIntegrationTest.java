package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.AnnulTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.HostedAdminResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.QueryTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

/**
 * @author Kristian Grossman-Madsen
 */
public class AnnulTransactionIntegrationTest {    
	
    @Test
    public void test_doRequest_returns_AnnulTransactionResponse_failure() {
    	
    	AnnulTransactionRequest request = new AnnulTransactionRequest(SveaConfig.getDefaultConfig())
    		.setCountryCode(COUNTRYCODE.SE)
			.setTransactionId( "123456" )
		;
    	
    	AnnulTransactionResponse response = request.doRequest();

    	assertThat( response, instanceOf(AnnulTransactionResponse.class) );
        assertThat( response, instanceOf(HostedAdminResponse.class) );
          
        assertFalse( response.isOrderAccepted() );
    	assertEquals( response.getResultCode(), "128 (NO_SUCH_TRANS)" );      	
    }   
    
    @Test
    public void test_doRequest_returns_AnnulTransactionResponse_success() {
    	HostedPaymentResponse order = (HostedPaymentResponse)TestingTool.createCardTestOrder("test_doRequest_returns_AnnulTransactionResponse_success");
        assertTrue(order.isOrderAccepted());
        
        AnnulTransactionRequest request = new AnnulTransactionRequest( SveaConfig.getDefaultConfig() )
        	.setTransactionId( order.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        AnnulTransactionResponse response = request.doRequest();
        
    	assertThat( response, instanceOf(AnnulTransactionResponse.class) );
        assertThat( response, instanceOf(HostedAdminResponse.class) );
        
        assertTrue( response.isOrderAccepted() ); 
		assertEquals( order.getTransactionId(),response.getTransactionId() );
		
		// query result
        QueryOrderBuilder query = new QueryOrderBuilder( SveaConfig.getDefaultConfig() )
        	.setTransactionId( order.getTransactionId()  )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse answer = query.queryCardOrder().doRequest();         
        
        assertTrue( answer.isOrderAccepted() ); 

		assertEquals("ANNULLED", answer.getStatus());
    }
}
