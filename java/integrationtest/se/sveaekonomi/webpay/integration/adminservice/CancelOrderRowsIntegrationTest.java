package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.response.adminservice.CancelOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class CancelOrderRowsIntegrationTest {

	// handles errors
	@Test
    public void test_cancel_nonexistant_orderid_returns_error() {
    	
        // cancel first order row and assert the response
        CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId( 999999L )
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setRowToCancel(1)
        ;
        CancelOrderRowsResponse response = builder.cancelInvoiceOrderRows().doRequest();
        
        assertFalse(response.isOrderAccepted());        
        assertEquals( "20004", response.getResultCode() );
        assertEquals( "Order does not exist.", response.getErrorMessage() );
	} 
	
	// invoice	

	
	@Test
    public void test_cancel_single_invoice_orderRow_returns_accepted() {
    	
    	// create an order using defaults
        CreateOrderBuilder orderRequest = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
                )
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)

	            .addOrderRow( WebPayItem.orderRow()		// row total 250.00
	            	.setArticleNumber("1")
	            	.setQuantity(2.0)	            	
	        		.setAmountExVat(100.00)
	        		.setDescription("Specification")
	        		.setName("Product")
	        		.setUnit("st")
	        		.setVatPercent(25)
	        		.setDiscountPercent(0.0)
	    		)
                .addOrderRow( WebPayItem.orderRow()		// row total 20.00
            		.setDescription("second row")
            		.setQuantity(1.0)
            		.setAmountExVat(16.00)
            		.setVatPercent(25)
        		)    
        		.addOrderRow( WebPayItem.orderRow()		// row total 30.00
            		.setDescription("third row")
            		.setQuantity(1.0)
            		.setAmountExVat(24.00)
            		.setVatPercent(25)
        		)  										// order total 300.00
    	;    	
        CreateOrderResponse order = orderRequest.useInvoicePayment().doRequest();
        assertTrue(order.isOrderAccepted());

        // cancel first order row and assert the response
        CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
        	.setOrderId(order.getOrderId())
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setRowToCancel(1)
        ;
        CancelOrderRowsResponse response = builder.cancelInvoiceOrderRows().doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CancelOrderRowsResponse );
	}   

	@Test
    public void test_deliver_multiple_invoice_orderRows_returns_accepted_with_invoiceId() {

    	// create an order using defaults
        CreateOrderBuilder orderRequest = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
                )
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)

	            .addOrderRow( WebPayItem.orderRow()		// row total 250.00
	            	.setArticleNumber("1")
	            	.setQuantity(2.0)	            	
	        		.setAmountExVat(100.00)
	        		.setDescription("Specification")
	        		.setName("Product")
	        		.setUnit("st")
	        		.setVatPercent(25)
	        		.setDiscountPercent(0.0)
	    		)
                .addOrderRow( WebPayItem.orderRow()		// row total 20.00
            		.setDescription("second row")
            		.setQuantity(1.0)
            		.setAmountExVat(16.00)
            		.setVatPercent(25)
        		)    
        		.addOrderRow( WebPayItem.orderRow()		// row total 30.00
            		.setDescription("third row")
            		.setQuantity(1.0)
            		.setAmountExVat(24.00)
            		.setVatPercent(25)
        		)  										// order total 300.00
    	;    	
        CreateOrderResponse order = orderRequest.useInvoicePayment().doRequest();
        assertTrue(order.isOrderAccepted());

        // cancel first order row and assert the response
        CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
        	.setOrderId(order.getOrderId())
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setRowToCancel(1).setRowToCancel(2)
        ;
        CancelOrderRowsResponse response = builder.cancelInvoiceOrderRows().doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CancelOrderRowsResponse );
	}  
	
	// paymentplan 
    @Test
    public void test_cancel_single_paymentPlan_orderRow_returns_accepted() {
    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_cancelOrderRows_cancelPaymentPlanOrderRows_cancel_all_rows");
        assertTrue(order.isOrderAccepted());

        // deliver first order row and assert the response
        CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
        	.setOrderId(order.getOrderId())
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setRowToCancel(1)
        ;
    	CancelOrderRowsResponse response = builder.cancelPaymentPlanOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CancelOrderRowsResponse );
    }   	
    
	// card	
    //TODO
    // uses webdriver, see WebPayAdminWebdriverTest.java
}
