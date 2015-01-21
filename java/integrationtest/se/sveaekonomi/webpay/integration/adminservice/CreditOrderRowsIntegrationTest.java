package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class CreditOrderRowsIntegrationTest {

    // handles errors
	@Test
    public void test_credit_nonexistant_orderid_returns_error() {
    	
        // credit first order row and assert the response
        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
            .setInvoiceId( "999999" )
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setRowToCredit(1)
        ;
        CreditOrderRowsResponse response = builder.creditInvoiceOrderRows().doRequest();
        
        assertFalse(response.isOrderAccepted());        
        assertEquals( "24001", response.getResultCode() );
        assertEquals( "Invoice not found", response.getErrorMessage() );
	} 
	
	// invoice	
	@Test
    public void test_creditOrderRows_creditInvoiceOrderRows_credit_all_rows() {
		    	
		// create an order using defaults
		CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_creditOrderRows_creditInvoiceOrderRows_credit_all_rows");
		assertTrue(order.isOrderAccepted());
 
        // deliver first order row and assert the response
        DeliverPartialResponse deliver = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setRowToDeliver(1)
            .deliverInvoiceOrderRows()
            	.doRequest();        
        assertTrue(deliver.isOrderAccepted());                		
		
		// credit first order row and assert the response
        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setInvoiceId( String.valueOf(deliver.getInvoiceId()) )
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode( COUNTRYCODE.SE )
            .setRowToCredit(1)
		;
        CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
        CreditOrderRowsResponse response = request.doRequest();
        assertTrue(response.isOrderAccepted());    
        assertEquals(Double.valueOf(250.00), response.getAmount());
        assertEquals(String.valueOf(order.orderId), response.getOrderId());		// TODO refactor to order.getOrderId() returning String!
        assertNotNull(response.getCreditInvoiceId());
        assertEquals(ORDERTYPE.Invoice, response.getOrderType());
        assertEquals("79021", response.getClientId());
    }
	
//	@Test
//    public void test_credit_single_invoice_orderRow_returns_accepted() {
//    	
//    	// create an order using defaults
//        CreateOrderBuilder orderRequest = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                .addCustomerDetails(WebPayItem.individualCustomer()
//                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//                )
//                .setCountryCode(TestingTool.DefaultTestCountryCode)
//                .setOrderDate(TestingTool.DefaultTestDate)
//
//	            .addOrderRow( WebPayItem.orderRow()		// row total 250.00
//	            	.setArticleNumber("1")
//	            	.setQuantity(2.0)	            	
//	        		.setAmountExVat(100.00)
//	        		.setDescription("Specification")
//	        		.setName("Product")
//	        		.setUnit("st")
//	        		.setVatPercent(25)
//	        		.setDiscountPercent(0.0)
//	    		)
//                .addOrderRow( WebPayItem.orderRow()		// row total 20.00
//            		.setDescription("second row")
//            		.setQuantity(1.0)
//            		.setAmountExVat(16.00)
//            		.setVatPercent(25)
//        		)    
//        		.addOrderRow( WebPayItem.orderRow()		// row total 30.00
//            		.setDescription("third row")
//            		.setQuantity(1.0)
//            		.setAmountExVat(24.00)
//            		.setVatPercent(25)
//        		)  										// order total 300.00
//    	;    	
//        CreateOrderResponse order = orderRequest.useInvoicePayment().doRequest();
//        assertTrue(order.isOrderAccepted());
//
//        // deliver first and second order row and assert the response
//        DeliverPartialResponse deliver = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
//            .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
//            .setCountryCode(TestingTool.DefaultTestCountryCode)	
//            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
//            .setRowToDeliver(1).setRowToDeliver(2)
//            .deliverInvoiceOrderRows()
//            	.doRequest();
//        
//        assertTrue(deliver.isOrderAccepted());        
//        assertTrue(deliver instanceof DeliverPartialResponse );
//        
//        // credit first order row and assert the response
//        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
//            .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
//            .setCountryCode(TestingTool.DefaultTestCountryCode)	
//            .setRowToCredit(1)
//        ;
//        CreditOrderRowsResponse response = builder.creditInvoiceOrderRows().doRequest();
//        
//        assertTrue(response.isOrderAccepted());        
//        assertTrue(response instanceof CreditOrderRowsResponse );
//	}   
//
//	@Test
//    public void test_deliver_multiple_invoice_orderRows_returns_accepted_with_invoiceId() {
//
//    	// create an order using defaults
//        CreateOrderBuilder orderRequest = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                .addCustomerDetails(WebPayItem.individualCustomer()
//                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
//                )
//                .setCountryCode(TestingTool.DefaultTestCountryCode)
//                .setOrderDate(TestingTool.DefaultTestDate)
//
//	            .addOrderRow( WebPayItem.orderRow()		// row total 250.00
//	            	.setArticleNumber("1")
//	            	.setQuantity(2.0)	            	
//	        		.setAmountExVat(100.00)
//	        		.setDescription("Specification")
//	        		.setName("Product")
//	        		.setUnit("st")
//	        		.setVatPercent(25)
//	        		.setDiscountPercent(0.0)
//	    		)
//                .addOrderRow( WebPayItem.orderRow()		// row total 20.00
//            		.setDescription("second row")
//            		.setQuantity(1.0)
//            		.setAmountExVat(16.00)
//            		.setVatPercent(25)
//        		)    
//        		.addOrderRow( WebPayItem.orderRow()		// row total 30.00
//            		.setDescription("third row")
//            		.setQuantity(1.0)
//            		.setAmountExVat(24.00)
//            		.setVatPercent(25)
//        		)  										// order total 300.00
//    	;    	
//        CreateOrderResponse order = orderRequest.useInvoicePayment().doRequest();
//        assertTrue(order.isOrderAccepted());
//
//        // credit first order row and assert the response
//        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
//            .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
//            .setCountryCode(TestingTool.DefaultTestCountryCode)	
//            .setRowToCredit(1).setRowToCredit(2)
//        ;
//        CreditOrderRowsResponse response = builder.creditInvoiceOrderRows().doRequest();
//        
//        assertTrue(response.isOrderAccepted());        
//        assertTrue(response instanceof CreditOrderRowsResponse );
//	}  
//	
//	// paymentplan 
//    @Test
//    public void test_credit_single_paymentPlan_orderRow_returns_accepted() {
//    	
//    	// create an order using defaults
//    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_creditOrderRows_creditPaymentPlanOrderRows_credit_all_rows");
//        assertTrue(order.isOrderAccepted());
//
//        // deliver first order row and assert the response
//        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
//            .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
//            .setCountryCode(TestingTool.DefaultTestCountryCode)	
//            .setRowToCredit(1)
//        ;
//    	CreditOrderRowsResponse response = builder.creditPaymentPlanOrderRows().doRequest();
//        assertTrue(response.isOrderAccepted());        
//        assertTrue(response instanceof CreditOrderRowsResponse );
//    }   	
    
	// card	
    //TODO
    // uses webdriver, see WebPayAdminWebdriverTest.java
}
