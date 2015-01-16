package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DeliverOrderRowsIntegrationTest {

	// invoice
	@Test
    public void test_deliverOrderRows_deliverInvoiceOrderRows() {
    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_cancelOrder_cancelInvoiceOrder");
        assertTrue(order.isOrderAccepted());

        // deliver first order row and assert the response
        DeliverOrderRowsResponse response = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setInvoiceDistributionType("Post")					// TODO use enum
            .setRowToDeliver(1)
            .deliverInvoiceOrderRows()
            	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof DeliverOrderRowsResponse );
    }  
	
	// card	
    // uses webdriver, see WebPayAdminWebdriverTest.java
}
