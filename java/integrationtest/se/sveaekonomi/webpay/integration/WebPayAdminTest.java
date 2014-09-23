package se.sveaekonomi.webpay.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class WebPayAdminTest {
	
    @Test
    public void test_cancelOrder_cancelInvoiceOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_cancelOrder_cancelInvoiceOrder");
        assertTrue(order.isOrderAccepted());

        // test WebPay::closeOrder
        CloseOrderResponse response = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .cancelInvoiceOrder()
                	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
    }    
    
    @Test
    public void test_cancelOrder_cancelPaymentPlanOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_cancelOrder_cancelPaymentPlanOrder");
        assertTrue(order.isOrderAccepted());

        // test WebPay::closeOrder
        CloseOrderResponse response = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .cancelPaymentPlanOrder()
                	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
    }       
}
