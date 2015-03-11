package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.response.adminservice.DeliverOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DeliverPartialIntegrationTest {

	// invoice
	@Test
    public void test_deliver_single_invoice_orderRow_returns_accepted_with_invoiceId() {
    	
    	// create an order using defaults
        CreateOrderBuilder orderRequest = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
                )
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)

	            .addOrderRow( WebPayItem.orderRow()
	            	.setArticleNumber("1")
	            	.setQuantity(2.0)	            	
	        		.setAmountExVat(100.00)
	        		.setDescription("Specification")
	        		.setName("Product")
	        		.setUnit("st")
	        		.setVatPercent(25)
	        		.setDiscountPercent(0.0)
	    		)
                .addOrderRow( WebPayItem.orderRow()
            		.setDescription("second row")
            		.setQuantity(1.0)
            		.setAmountExVat(16.00)
            		.setVatPercent(25)
        		)    
        		.addOrderRow( WebPayItem.orderRow()
            		.setDescription("third row")
            		.setQuantity(1.0)
            		.setAmountExVat(24.00)
            		.setVatPercent(25)
        		)  
    	;    	
        CreateOrderResponse order = orderRequest.useInvoicePayment().doRequest();
        assertTrue(order.isOrderAccepted());

        // deliver first order row and assert the response
        DeliverOrderRowsResponse response = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId( order.getOrderId() )
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setRowToDeliver(1)
            .deliverInvoiceOrderRows()
            	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof DeliverOrderRowsResponse );
        
        assertEquals(order.getOrderId(), response.getOrderId());
        assertEquals(250.00, response.getAmount(), 0.0001);
        assertNotNull(response.invoiceId);
        assertNull(response.contractNumber);
        assertEquals(ORDERTYPE.Invoice, response.getOrderType());
        assertNotNull(response.invoiceId);
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

	            .addOrderRow( WebPayItem.orderRow()
	            	.setArticleNumber("1")
	            	.setQuantity(2.0)	            	
	        		.setAmountExVat(100.00)
	        		.setDescription("Specification")
	        		.setName("Product")
	        		.setUnit("st")
	        		.setVatPercent(25)
	        		.setDiscountPercent(0.0)
	    		)
                .addOrderRow( WebPayItem.orderRow()
            		.setDescription("second row")
            		.setQuantity(1.0)
            		.setAmountExVat(16.00)
            		.setVatPercent(25)
        		)    
        		.addOrderRow( WebPayItem.orderRow()
            		.setDescription("third row")
            		.setQuantity(1.0)
            		.setAmountExVat(24.00)
            		.setVatPercent(25)
        		)  
    	;    	
        CreateOrderResponse order = orderRequest.useInvoicePayment().doRequest();
        assertTrue(order.isOrderAccepted());

        // deliver first order row and assert the response
        DeliverOrderRowsResponse response = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId( order.getOrderId() )
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setRowToDeliver(1).setRowToDeliver(2)
            .deliverInvoiceOrderRows()
            	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof DeliverOrderRowsResponse );
        
        assertEquals(order.getOrderId(), response.getOrderId());
        assertEquals(270.00, response.getAmount(), 0.0001);
        assertNotNull(response.invoiceId);
        assertNull(response.contractNumber);
        assertEquals(ORDERTYPE.Invoice, response.getOrderType());
        assertNotNull(response.invoiceId);
	}  
	
	// card	
    // uses webdriver, see WebPayAdminWebdriverTest.java
}
