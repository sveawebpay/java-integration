package se.sveaekonomi.webpay.integration;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.adminservice.GetOrdersResponse;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.AnnulTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.CreditTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.LowerTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.QueryTransactionResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;


/**
 * contains end-to-end integration tests of WebPayAdmin entrypoints
 * used as acceptance test for package functionality, performs requests to Svea services (test environment)
 * 
 * see unit tests for validation of required entrypoint methods for the various payment methods/customers/countries
 * 
 * @author Kristian Grossman-Madsen
 */
public class WebPayAdminWebdriverTest {    
	
    /// WebPayAdmin.cancelOrder() --------------------------------------------------------------------------------------
	// invoice
    @Test
    public void test_cancelOrder_cancelInvoiceOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_cancelOrder_cancelInvoiceOrder");
        assertTrue(order.isOrderAccepted());

        // do cancelOrder request and assert the response
        CloseOrderResponse response = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .cancelInvoiceOrder()
                	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CloseOrderResponse );
    }    
	// payment plan    
    @Test
    public void test_cancelOrder_cancelPaymentPlanOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_cancelOrder_cancelPaymentPlanOrder");
        assertTrue(order.isOrderAccepted());

        // do cancelOrder request and assert the response
        CloseOrderResponse response = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .cancelPaymentPlanOrder()
                	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CloseOrderResponse );
    }       
    // card
    @Test
    public void test_cancelOrder_cancelCardOrder() {
    	    	
    	// create an order using defaults
    	HostedPaymentResponse order = (HostedPaymentResponse)TestingTool.createCardTestOrder("test_cancelOrder_cancelCardOrder");
        assertTrue(order.isOrderAccepted());

        // do cancelOrder request and assert the response
        AnnulTransactionResponse response = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
	        .setTransactionId(order.getTransactionId())
	        .setCountryCode(TestingTool.DefaultTestCountryCode)
	        .cancelCardOrder()
        		.doRequest();
    
        assertTrue(response.isOrderAccepted());  
        assertTrue(response instanceof AnnulTransactionResponse );
 
    }

    /// WebPayAdmin.queryOrder() ---------------------------------------------------------------------------------------
    // .queryInvoiceOrder
    @Test
    public void test_queryOrder_queryInvoiceOrder() {

		// create order
        CreateOrderBuilder builder = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))                
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
                )
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)
    	;
        
        CreateOrderResponse order = builder.useInvoicePayment().doRequest();
        assertTrue(order.isOrderAccepted());
        
        // query order
        QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setOrderId( 999999 )//order.orderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();

			assertTrue( response.isOrderAccepted() );     
	   		assertEquals( String.valueOf(order.orderId), response.getOrderId() );
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }
    // paymentplan
    // TODO
    
    // .queryCardOrder
    @Test
    public void test_queryOrder_queryCardOrder() {

    	// create an order using defaults
    	HostedPaymentResponse order = (HostedPaymentResponse)TestingTool.createCardTestOrder("test_cancelOrder_cancelCardOrder");
        assertTrue(order.isOrderAccepted());
        
        // query order
        QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( order.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse response = queryOrderBuilder.queryCardOrder().doRequest();         
        
        assertTrue( response.isOrderAccepted() );     
   		assertEquals( order.getTransactionId(), response.getTransactionId() );
    }
    // directbank
    // TODO

    /// WebPayAdmin.deliverOrderRows()
    // invoice
    // TODO
    // paymentplan
    // TODO
    // card
    @Test
    public void test_deliverOrderRows_deliverCardOrderRows_deliver_all_rows() {
    	
    	HostedPaymentResponse order = TestingTool.createCardTestOrder("test_deliverOrderRows_deliverCardOrderRows_deliver_all_rows");
        assertTrue(order.isOrderAccepted());

        QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(order.getTransactionId())
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse queryResponse = queryOrderBuilder.queryCardOrder().doRequest();         
        assertTrue( queryResponse.isOrderAccepted() );             
        assertEquals( 1, queryResponse.getNumberedOrderRows().get(0).getRowNumber() );

        DeliverOrderRowsBuilder deliverRequest = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( queryResponse.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .setRowToDeliver(1)
		    .addNumberedOrderRows(queryResponse.getNumberedOrderRows()) 
		;
        ConfirmTransactionResponse response = deliverRequest.deliverCardOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
    } 
    
    @Test
    public void test_deliverOrderRows_deliverCardOrderRows_deliver_first_and_second_row_of_three() {
    	
    	HostedPaymentResponse order = TestingTool.createCardTestOrderWithThreeRows("test_deliverOrderRows_deliverCardOrderRows_deliver_first_and_second_row_of_three");
        assertTrue(order.isOrderAccepted());

        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(order.getTransactionId())
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();         
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );

        ArrayList<Integer> indexes = new ArrayList<Integer>();
        indexes.add(1);
        indexes.add(2);
        DeliverOrderRowsBuilder deliverRequest = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
            .setRowToDeliver(1)
		    .setRowsToDeliver( indexes )
		    .addNumberedOrderRows(originalOrder.getNumberedOrderRows()) 
		;

        ConfirmTransactionResponse response = deliverRequest.deliverCardOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        

        QueryOrderBuilder queryDeliveredOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( order.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
        ;             
        QueryTransactionResponse deliveredOrder = queryDeliveredOrder.queryCardOrder().doRequest();                
        assertTrue( deliveredOrder.isOrderAccepted() );        
		assertEquals("75000", deliveredOrder.getAmount());	
		assertEquals("50000", deliveredOrder.getAuthorizedAmount());    
    }
        
    /// WebPayAdmin.cancelOrderRows() --------------------------------------------------------------------------------------------	
    // card
    @Test
    public void test_cancelOrderRows_cancelCardOrderRows_cancel_all_rows() {
    	
    	// create an order using defaults
    	HostedPaymentResponse order = TestingTool.createCardTestOrder("test_cancelOrderRows_cancelCardOrderRows_cancel_all_rows");
        assertTrue(order.isOrderAccepted());
        
        // first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(order.getTransactionId())
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();                 
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );

        // do cancelOrderRows request and assert the response
        CancelOrderRowsBuilder cancelRequest = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .setRowToCancel(1)	// only row in order
		    .addNumberedOrderRows(originalOrder.getNumberedOrderRows()) 
		;
        LowerTransactionResponse response = cancelRequest.cancelCardOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
        
        // query cancelled order and assert amounts
        QueryOrderBuilder queryCancelledOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(order.getTransactionId())
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse cancelledOrder = queryCancelledOrder.queryCardOrder().doRequest();                 
        assertTrue(cancelledOrder.isOrderAccepted());
        assertEquals( "0", cancelledOrder.getAuthorizedAmount());
        assertEquals( "ANNULLED", cancelledOrder.getStatus()); // i.e. loweredamount caused order authorizedamount = 0        
    }
    
    @Test
    public void test_cancelOrderRows_cancelCardOrderRows_cancel_first_and_second_row_of_three() {
    	
    	// create an order using defaults
    	HostedPaymentResponse order = TestingTool.createCardTestOrderWithThreeRows("test_cancelOrderRows_cancelCardOrderRows_cancel_first_and_second_row_of_three");
        assertTrue(order.isOrderAccepted());
        
        // first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(order.getTransactionId())
            .setCountryCode( COUNTRYCODE.SE )
        ;   
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();                 
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );
        
        // do cancelOrderRows request and assert the response
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        indexes.add(1);
        indexes.add(2);    
        CancelOrderRowsBuilder cancelRequest = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .setRowsToCancel( indexes )
		    .addNumberedOrderRows(originalOrder.getNumberedOrderRows()) 
		;         
        LowerTransactionResponse response = cancelRequest.cancelCardOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        	

        // query cancelled order and assert amounts
        QueryOrderBuilder queryCancelledOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(order.getTransactionId())
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse cancelledOrder = queryCancelledOrder.queryCardOrder().doRequest();                 
        assertTrue(cancelledOrder.isOrderAccepted());
		assertEquals("75000", cancelledOrder.getAmount());	
		assertEquals("25000", cancelledOrder.getAuthorizedAmount());    
    }    

    /// WebPayAdmin.creditOrderRows() --------------------------------------------------------------------------------------------	
    // card
    @Test
    public void test_creditOrderRows_creditCardOrderRows_credit_original_order_first_and_second_rows_of_three() {
    	
    	// use an existing captured order (status SUCCESS), as we can't do a capture on an order via the webservice
    	String capturedOrderId = "590775";

    	// first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ; 
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();                 
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );
        
        // do creditOrderRows request and assert the response
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        indexes.add(1);
        indexes.add(2);    
        CreditOrderRowsBuilder creditRequest = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setOrderId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
            //.setRowToCredit(1)
            //.setRowToCredit(2) // => 1,2
		    .setRowsToCredit( indexes )	// => 1,2
		    .addNumberedOrderRows(originalOrder.getNumberedOrderRows()) 
		;
        CreditTransactionResponse response = creditRequest.creditCardOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        	

        // query credited order and assert amounts
        QueryOrderBuilder queryCancelledOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(capturedOrderId)
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse creditedOrder = queryCancelledOrder.queryCardOrder().doRequest();                 
        assertTrue(creditedOrder.isOrderAccepted());
        assertEquals( (Long)(Long.valueOf(originalOrder.getCreditedAmount())+25000+25000), (Long)Long.valueOf(creditedOrder.getCreditedAmount()));
    	assertEquals( "CREDSUCCESS", creditedOrder.getCreditstatus());
    }    

    @Test
    public void test_creditOrderRows_creditCardOrderRows_credit_original_order_row_and_new_custom_order_row() {
    	
    	// use an existing captured order (status SUCCESS), as we can't do a capture on an order via the webservice
    	String capturedOrderId = "590775";

    	// first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();                 
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );

        // do creditOrderRows request and assert the response
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;

		CreditOrderRowsBuilder creditRequest = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .setRowToCredit(1)	// 
		    .addNumberedOrderRows(originalOrder.getNumberedOrderRows()) 
		    .addCreditOrderRow( customAmountRow )
	    ;
        CreditTransactionResponse response = creditRequest.creditCardOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
        
        // query credited order and assert amounts
        QueryOrderBuilder queryCreditedOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse creditedOrder = queryCreditedOrder.queryCardOrder().doRequest();                 
        assertTrue(creditedOrder.isOrderAccepted());
        assertEquals( (Long)(Long.valueOf(originalOrder.getCreditedAmount())+25000+11000), (Long)Long.valueOf(creditedOrder.getCreditedAmount()));
    	assertEquals( "CREDSUCCESS", creditedOrder.getCreditstatus());    	
	}
    
    @Test
    public void test_creditOrderRows_creditCardOrderRows_credit_only_new_custom_order_row() {
    	
    	// use an existing captured order (status SUCCESS), as we can't do a capture on an order via the webservice
    	String capturedOrderId = "590775";

    	// first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();                 
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );

        // do creditOrderRows request and assert the response
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;

		CreditOrderRowsBuilder creditRequest = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .addCreditOrderRow( customAmountRow )
	    ;
        CreditTransactionResponse response = creditRequest.creditCardOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
        
        // query credited order and assert amounts
        QueryOrderBuilder queryCreditedOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse creditedOrder = queryCreditedOrder.queryCardOrder().doRequest();                 
        assertTrue(creditedOrder.isOrderAccepted());
        assertEquals( (Long)(Long.valueOf(originalOrder.getCreditedAmount())+11000), (Long)Long.valueOf(creditedOrder.getCreditedAmount()));
    	assertEquals( "CREDSUCCESS", creditedOrder.getCreditstatus());    	
	}

    // direct bank
    @Test
    public void test_creditOrderRows_creditDirectBankOrderRows_credit_original_order_first_and_second_rows_of_three() {    	
    	String capturedOrderId = "590801";

    	// first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ; 
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();                 
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );
        
        // do creditOrderRows request and assert the response
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        indexes.add(1);
        ArrayList<Integer> indexes2 = new ArrayList<Integer>();
        indexes2.add(2);    
        CreditOrderRowsBuilder creditRequest = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setOrderId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .setRowsToCredit( indexes )
		    .setRowsToCredit( indexes2 ) // test adds upp to 1,2
		    .addNumberedOrderRows(originalOrder.getNumberedOrderRows()) 
		;
        CreditTransactionResponse response = creditRequest.creditDirectBankOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        	

        // query credited order and assert amounts
        QueryOrderBuilder queryCancelledOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(capturedOrderId)
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse creditedOrder = queryCancelledOrder.queryCardOrder().doRequest();                 
        assertTrue(creditedOrder.isOrderAccepted());
        assertEquals( (Long)(Long.valueOf(originalOrder.getCreditedAmount())+25000+25000), (Long)Long.valueOf(creditedOrder.getCreditedAmount()));
    	assertEquals( "CREDSUCCESS", creditedOrder.getCreditstatus());
    }    

    @Test
    public void test_creditOrderRows_creditDirectBankOrderRows_credit_original_order_row_and_new_custom_order_row() {
    	String capturedOrderId = "590801";
    	
    	// first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();                 
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );

        // do creditOrderRows request and assert the response
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;

		CreditOrderRowsBuilder creditRequest = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .setRowToCredit(1)	// 
		    .addNumberedOrderRows(originalOrder.getNumberedOrderRows()) 
		    .addCreditOrderRow( customAmountRow )
	    ;
        CreditTransactionResponse response = creditRequest.creditDirectBankOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
        
        // query credited order and assert amounts
        QueryOrderBuilder queryCreditedOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse creditedOrder = queryCreditedOrder.queryCardOrder().doRequest();                 
        assertTrue(creditedOrder.isOrderAccepted());
        assertEquals( (Long)(Long.valueOf(originalOrder.getCreditedAmount())+25000+11000), (Long)Long.valueOf(creditedOrder.getCreditedAmount()));
    	assertEquals( "CREDSUCCESS", creditedOrder.getCreditstatus());    	
	}
    
    @Test
    public void test_creditOrderRows_creditDirectBankOrderRows_credit_only_new_custom_order_row() {
    	String capturedOrderId = "590801";

    	// first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();                 
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );

        // do creditOrderRows request and assert the response
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;

		CreditOrderRowsBuilder creditRequest = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .addCreditOrderRow( customAmountRow )
	    ;
        CreditTransactionResponse response = creditRequest.creditDirectBankOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
        
        // query credited order and assert amounts
        QueryOrderBuilder queryCreditedOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( capturedOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse creditedOrder = queryCreditedOrder.queryCardOrder().doRequest();                 
        assertTrue(creditedOrder.isOrderAccepted());
        assertEquals( (Long)(Long.valueOf(originalOrder.getCreditedAmount())+11000), (Long)Long.valueOf(creditedOrder.getCreditedAmount()));
    	assertEquals( "CREDSUCCESS", creditedOrder.getCreditstatus());    	
	}
}
