package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.UpdateOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERROWSTATUS;

public class UpdateOrderRowsIntegrationTest {

	// handles errors
	@Test
    public void test_update_nonexistant_orderid_returns_error() {
    	
		UpdateOrderRowsBuilder builder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( "999999")              
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.99)	// 99.99inc = 80.637096 @24% => 80.64ex @24% = 99.9936 => 99.99inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			// TODO check w/Robin re: status of these if not set in request?
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse response = builder.updateInvoiceOrderRows().doRequest();        
        assertFalse(response.isOrderAccepted());        
        assertEquals( "20004", response.getResultCode() );
        assertEquals( "Order does not exist.", response.getErrorMessage() );
	} 
	
	// invoice
	// payment plan
}
