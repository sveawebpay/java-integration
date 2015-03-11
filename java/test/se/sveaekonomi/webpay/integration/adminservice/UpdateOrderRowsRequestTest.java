package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.UpdateOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;

public class UpdateOrderRowsRequestTest {

	/// validation
	@Test 
    public void test_validates_all_required_methods_for_updateOrderRows_updateInvoiceOrderRows_single_row() {
    	
		UpdateOrderRowsBuilder builder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( 999999L)              
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()	// dummy, so no attributes set -- we only validate presence of numberedOrderRow objects
    		)
		;
		try {
			UpdateOrderRowsRequest request = builder.updateInvoiceOrderRows();
			request.validateOrder();
		}
		catch (ValidationException e){			
			// fail on validation error
			fail("Unexpected ValidationException.");
		}	 				
    }
	
	@Test
	public void test_validates_missing_required_methods_for_updatOrderRows_updateInvoiceOrderRows() {

		UpdateOrderRowsBuilder builder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
			//.setOrderId( 999999L)              
			//.setCountryCode( COUNTRYCODE.SE ) 
			//.addUpdateOrderRow( 
			//	WebPayItem.numberedOrderRow()	// dummy, so no attributes set -- we only validate presence
			//)
		;    

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			UpdateOrderRowsRequest request = builder.updateInvoiceOrderRows();
			request.validateOrder();
			// fail if validation passes
	        fail();	
        }
		catch (ValidationException e){			
			assertEquals( 
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - CountryCode is required, use setCountryCode().\n" +
        		"MISSING VALUE - updateOrderRows is required, use method addUpdateOrderRow()/addUpdateOrderRows().\n",
    			e.getMessage()
    		);
        }	
	}		
}
