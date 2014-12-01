package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.AnnulTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

/**
 * @author Kristian Grossman-Madsen
 */
public class AnnulTransactionIntegrationTest {    
	
    @Test
    public void test_annulTransaction() {
    	// create an order using defaults
    	HostedPaymentResponse order = (HostedPaymentResponse)TestingTool.createCardTestOrder("test_cancelOrder_cancelCardOrder");
        assertTrue(order.isOrderAccepted());
        
        // query order
        AnnulTransactionRequest request = new AnnulTransactionRequest( SveaConfig.getDefaultConfig() )
        	.setTransactionId( order.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        AnnulTransactionResponse response = request.doRequest();
        
        assertTrue( response.isOrderAccepted() ); 
		assertEquals( order.getTransactionId(),response.getTransactionId() );
		
    }
}
