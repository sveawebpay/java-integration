package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.adminservice.CreditOrderRowsRequest;
import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.CreditTransactionRequest;
import se.sveaekonomi.webpay.integration.order.handle.AddOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.UpdateOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class AddOrderRowsRequestTest {

	/// validation
	@Test 
    public void test_validates_all_required_methods_for_addOrderRows_addInvoiceOrderRows_single_row() {
    	
		AddOrderRowsBuilder builder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( "999999")              
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()	// dummy, so no attributes set -- we only validate presence
    		)
		;
		try {
			AddOrderRowsRequest request = builder.addInvoiceOrderRows();
			request.validateOrder();
		}
		catch (ValidationException e){			
			// fail on validation error
			fail("Unexpected ValidationException.");
		}	 				
    }
	
	@Test
	public void test_validates_missing_required_methods_for_addOrderRows_addInvoiceOrderRows() {

		AddOrderRowsBuilder builder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
			//.setOrderId( "999999")              
			//.setCountryCode( COUNTRYCODE.SE ) 
			//.addOrderRow( 
			//	WebPayItem.orderRow()	// dummy, so no attributes set -- we only validate presence
			//)
		;    

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			AddOrderRowsRequest request = builder.addInvoiceOrderRows();
			request.validateOrder();
			// fail if validation passes
	        fail();	
        }
		catch (ValidationException e){			
			assertEquals( 
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - CountryCode is required, use setCountryCode().\n" +
        		"MISSING VALUE - orderRows is required, use method addOrderRow()/addOrderRows().\n",
    			e.getMessage()
    		);
        }	
	}		
}
