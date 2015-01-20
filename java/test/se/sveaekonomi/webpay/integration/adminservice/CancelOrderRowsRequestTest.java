package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class CancelOrderRowsRequestTest {

	/// validation
	// invoice
	@Test 
	public void test_cancelOrderRows_cancelInvoiceOrderRows_validates_all_required_methods() {

        CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId( "999999" ) // dummy order id
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setRowToCancel(1)
        ;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CancelOrderRowsRequest request = builder.cancelInvoiceOrderRows();
			request.validateOrder();
		}
		catch (ValidationException e){			
			// fail on validation error
	        fail();
        }	
	}			

	@Test 
	public void test_deliverOrderRows_deliverInvoiceOrderRows_validates_all_missing_required_methods() {

        CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
            //.setOrderId( "999999" ) // dummy order id
            //.setCountryCode(TestingTool.DefaultTestCountryCode)	
            //.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            //.setRowToDeliver(1)
		;

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CancelOrderRowsRequest request = builder.cancelInvoiceOrderRows();
			request.validateOrder();
			// fail if validation passes
	        fail();	
        }
		catch (ValidationException e){			
			assertEquals( 
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - CountryCode is required, use setCountryCode().\n" +
        		"MISSING VALUE - rowIndexesToDeliver is required for deliverInvoiceOrderRows(). Use methods setRowToDeliver() or setRowsToDeliver().\n",
    			e.getMessage()
    		);
        }	
	}	
	
	// see GetOrdersRequestTest for example of how to handle SveaWebPayException thrown when using prepareRequest() which uses validateOrder()

	// card	
    // TODO!
}
