package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.adminservice.GetOrdersResponse;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.QueryTransactionResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.OrderRowStatus;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DeliverOrderRowsIntegrationTest {

	// invoice
    public void test_deliverOrderRows_deliverInvoiceOrderRows() {
    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_cancelOrder_cancelInvoiceOrder");
        assertTrue(order.isOrderAccepted());

        // deliver first order row and assert the response
        DeliverOrdersResponse response = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
                .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
                .setCountryCode(TestingTool.DefaultTestCountryCode)	
                .setInvoiceDistributionType("Post")					// TODO use enum
                .setRowToDeliver(1)
                .deliverInvoiceOrderRows()
                	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof DeliverOrdersResponse );
    }  
	
	// card	
    // uses webdriver, see WebPayAdminWebdriverTest.java
}
