package se.sveaekonomi.webpay.integration.hosted.hostedadmin;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

/**
 * @author Kristian Grossman-Madsen
 */
public class ConfirmTransactionIntegrationTest {    
	
    @Test
    public void test_doRequest_returns_ConfirmTransactionResponse_failure() {
    	
    	ConfirmTransactionRequest request = new ConfirmTransactionRequest(SveaConfig.getDefaultConfig())
	    	.setCountryCode(COUNTRYCODE.SE)
	    	.setTransactionId( "123456" )
	    	.setCaptureDate("14-12-01")
    	;
    	
        ConfirmTransactionResponse response = request.doRequest();

        // if we receive an error from the service, the integration test passes
        assertFalse( response.isOrderAccepted() );
    	assertEquals( response.getResultCode(), "128 (NO_SUCH_TRANS)" );      	
    }
	
    @Test
    public void test_doRequest_returns_ConfirmTransactionResponse_success() {

    	// see WebPayWebdriverTest test_deliverOrder_deliverCardOrder()
    	
    }  
   
    @Test
    public void test_doRequest_with_setAlsoDoLowerAmount_returns_ConfirmTransactionResponse_success() {
    	
    	// see WebPayAdminWebdriverTest test_deliverOrderRows_deliverCardOrderRows_deliver_first_and_second_row_of_three()
	
    }  

}
