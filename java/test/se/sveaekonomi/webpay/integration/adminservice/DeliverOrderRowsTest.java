package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DeliverOrderRowsTest {

    // WebPay.createOrder() --------------------------------------------------------------------------------------------	
	/// returned request class
	// TODO
	/// validation
	// invoice
	@Test 
	public void test_deliverOrderRows_deliverInvoiceOrderRows_validates_all_required_methods() {

        DeliverOrderRowsBuilder builder = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId( "999999" ) // dummy order id
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setInvoiceDistributionType("Post")					// TODO use enum
            .setRowToDeliver(1);

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			DeliverOrderRowsRequest request = builder.deliverInvoiceOrderRows();
			request.validateOrder();
		}
		catch (ValidationException e){			
			// fail on validation error
	        fail();
        }	
	}			

	@Test 
	public void test_deliverOrderRows_deliverInvoiceOrderRows_validates_all_missing_required_methods() {

        DeliverOrderRowsBuilder builder = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
            //.setOrderId( "999999" ) // dummy order id
            //.setCountryCode(TestingTool.DefaultTestCountryCode)	
            //.setInvoiceDistributionType("Post")					// TODO use enum
            //.setRowToDeliver(1)
		;

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			DeliverOrderRowsRequest request = builder.deliverInvoiceOrderRows();
			request.validateOrder();
		
			// fail if validation passes
	        fail();	
        }
		catch (ValidationException e){			
			assertEquals( 
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - CountryCode is required, use setCountryCode().\n" +
        		"MISSING VALUE - rowIndexesToDeliver is required for deliverInvoiceOrderRows(). Use methods setRowToDeliver() or setRowsToDeliver().\n" +
        		"MISSING VALUE - distributionType is required, use setInvoiceDistributionType().\n", 
    			e.getMessage()
    		);
        }	
	}	

	// card	
    // TODO!
}
