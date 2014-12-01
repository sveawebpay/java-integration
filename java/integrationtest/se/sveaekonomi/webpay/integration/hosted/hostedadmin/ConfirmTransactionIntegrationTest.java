package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

/**
 * @author Kristian Grossman-Madsen
 */
public class ConfirmTransactionIntegrationTest {    
	
    @Test
    public void test_ConfirmTransaction() {
    	HostedPaymentResponse order = (HostedPaymentResponse)TestingTool.createCardTestOrder("test_cancelOrder_cancelCardOrder");
        assertTrue(order.isOrderAccepted());
               
        ConfirmTransactionRequest request = new ConfirmTransactionRequest( SveaConfig.getDefaultConfig() )
        	.setCaptureDate( String.format("%tF", new Date()) ) //'t' => time, 'F' => ISO 8601 complete date formatted as "%tY-%tm-%td"
        	.setTransactionId( order.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
        ; 
        ConfirmTransactionResponse response = request.doRequest();
        
        assertTrue( response.isOrderAccepted() ); 
		assertEquals( order.getTransactionId(),response.getTransactionId() );		
    }
}
