package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
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
	// using setRowToCredit
	@Test
    public void test_creditOrderRows_creditInvoiceOrderRows_using_setRowToCredit() {
		    	
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
	
	// using addCreditOrderRow
	// exvat/exvat
	@Test
    public void test_creditOrderRows_creditInvoiceOrderRows_using_addCreditOrderRow_original_exvat_credit_exvat() {
		    	
		// create an order using defaults
        CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow( 
            		WebPayItem.orderRow()
                        .setArticleNumber("original")
                        .setName("Prod")
                        .setDescription("Specification")
                        .setAmountExVat(99.99)	// 79.99ex @24% = 123.9876 => 123.99inc
                        .setQuantity(1.0)
                        .setUnit("st")
                        .setVatPercent(24)
                        .setVatDiscount(0)
        		)
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
                )
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)
    	;
        CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
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
		
		// credit order row and assert the response
        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setInvoiceId( String.valueOf(deliver.getInvoiceId()) )
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode( COUNTRYCODE.SE )
            .addCreditOrderRow(
	    		WebPayItem.orderRow()
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(80.64)	// 99.99inc = 80.637096 @24% => 80.64ex @24% = 99.9936 => 99.99inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
            )
		;
        CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
        CreditOrderRowsResponse response = request.doRequest();
        assertTrue(response.isOrderAccepted());    
        
        assertEquals(Double.valueOf(99.99), response.getAmount());
        // TODO -- should response use getAmountExVat and getAmountIncVat instead, or should it use getAmount and getPriceIncludingVat for flag ??        
        assertEquals(String.valueOf(order.orderId), response.getOrderId());		// TODO refactor to order.getOrderId() returning String!
        assertNotNull(response.getCreditInvoiceId());        
        // TODO package currently does not support QueryInvoice to get actual credit invoice 
        System.out.println(
    		"\ntest_creditOrderRows_creditInvoiceOrderRows_using_addCreditOrderRow_original_exvat_credit_exvat :" + 
			response.getCreditInvoiceId()
		);        
    }	
 
	// exvat/incvat
	@Test
    public void test_creditOrderRows_creditInvoiceOrderRows_using_addCreditOrderRow_original_exvat_credit_incvat() {
		    	
		// create an order using defaults
        CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow( 
            		WebPayItem.orderRow()
                        .setArticleNumber("original")
                        .setName("Prod")
                        .setDescription("Specification")
                        .setAmountExVat(99.99)	// 79.99ex @24% = 123.9876 => 123.99inc
                        .setQuantity(1.0)
                        .setUnit("st")
                        .setVatPercent(24)
                        .setVatDiscount(0)
        		)
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
                )
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)
    	;
        CreateOrderResponse order = orderBuilder.useInvoicePayment().doRequest();
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
		
		// credit order row and assert the response
        CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setInvoiceId( String.valueOf(deliver.getInvoiceId()) )
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode( COUNTRYCODE.SE )
            .addCreditOrderRow(
	    		WebPayItem.orderRow()
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountIncVat(99.99)	// 99.99inc = 80.637096 @24% => 80.64ex @24% = 99.9936 => 99.99inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
            )
		;
        CreditOrderRowsRequest request = builder.creditInvoiceOrderRows();
        CreditOrderRowsResponse response = request.doRequest();
        assertTrue(response.isOrderAccepted());    
        
        assertEquals(Double.valueOf(99.99), response.getAmount());				// response	
        // TODO -- should response use getAmountExVat and getAmountIncVat instead, or should it use getAmount and getPriceIncludingVat for flag ??
        assertEquals(String.valueOf(order.orderId), response.getOrderId());		// TODO refactor to order.getOrderId() returning String!
        assertNotNull(response.getCreditInvoiceId());        
        // TODO package currently does not support QueryInvoice to get actual credit invoice 
        System.out.println(
    		"\ntest_creditOrderRows_creditInvoiceOrderRows_using_addCreditOrderRow_original_exvat_credit_exvat :" + 
			response.getCreditInvoiceId()
		);        
    }	
	// paymentplan 
	// NOT SUPPORTED
    
	// card	
    //TODO
    // uses webdriver, see WebPayAdminWebdriverTest.java
}
