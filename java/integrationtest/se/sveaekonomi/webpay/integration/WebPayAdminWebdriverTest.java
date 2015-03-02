package se.sveaekonomi.webpay.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.ArrayList;

import se.sveaekonomi.webpay.integration.adminservice.CreditOrderRowsRequest;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.AddOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.UpdateOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.response.adminservice.AddOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.adminservice.CancelOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.adminservice.CreditOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.adminservice.DeliverOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.adminservice.DeliverOrdersResponse;
import se.sveaekonomi.webpay.integration.response.adminservice.GetOrdersResponse;
import se.sveaekonomi.webpay.integration.response.adminservice.UpdateOrderRowsResponse;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.AnnulTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.CreditTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.LowerTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.QueryTransactionResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.ORDERROWSTATUS;
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
    // card (uses webdriver)
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
            .setOrderId( order.orderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryInvoiceOrder().doRequest();

			assertTrue( response.isOrderAccepted() );     
	   		assertEquals( order.getOrderId(), response.getOrderId() );
	   		
	   		// see GetOrdersIntegrationTest.java for detailed tests
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }
    // .queryPaymentPlanOrder
    @Test
    public void test_queryOrder_queryPaymentPlanOrder() {

		// create order
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_queryOrder_queryPaymentPlanOrder");
        assertTrue(order.isOrderAccepted());
        
        // query order
        QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setOrderId( order.orderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;
        try {
        	GetOrdersResponse response = queryOrderBuilder.queryPaymentPlanOrder().doRequest();

			assertTrue( response.isOrderAccepted() );     
	   		assertEquals( String.valueOf(order.orderId), response.getOrderId() );
        
	   		// see GetOrdersIntegrationTest.java for detailed tests
        }
        catch( Exception e ) {
        	System.out.println( e.getClass() + e.getMessage() );
        }
    }
    // .queryCardOrder (uses webdriver)
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
    @Test
    public void test_queryOrder_queryDirectBankOrder() {

    	// TODO set up webdriver create direct bank order, uses existing direct bank order below
    	String directOrderId = "594206";
    	
        // query order
        QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( directOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse response = queryOrderBuilder.queryDirectBankOrder().doRequest();         
        
        assertTrue( response.isOrderAccepted() );     
   		assertEquals( directOrderId, response.getTransactionId() );
    }

    /// WebPayAdmin.deliverOrderRows() ---------------------------------------------------------------------------------	
    //  .deliverInvoiceOrderRows
    @Test
    public void test_deliverOrderRows_deliverInvoiceOrderRows() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_cancelOrder_cancelInvoiceOrder");
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
    }    
    //  .deliverCardOrderRows (uses webdriver)
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
        
    /// WebPayAdmin.cancelOrderRows() ----------------------------------------------------------------------------------
    // .cancelInvoiceOrderRows
    @Test
    public void test_cancelOrderRows_cancelInvoiceOrderRows_cancel_all_rows() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_cancelOrderRows_cancelInvoiceOrderRows_cancel_all_rows");
        assertTrue(order.isOrderAccepted());

        // deliver first order row and assert the response
        CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setRowToCancel(1) // only row
        ;        
        CancelOrderRowsResponse response = builder.cancelInvoiceOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CancelOrderRowsResponse );
    }    
    // paymentplan
    @Test
    public void test_cancelOrderRows_cancelPaymentPlanOrderRows_cancel_all_rows() {
    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_cancelOrderRows_cancelPaymentPlanOrderRows_cancel_all_rows");
        assertTrue(order.isOrderAccepted());

        // deliver first order row and assert the response
        CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
            .setOrderId(String.valueOf(order.orderId))			// TODO add getters/setters to CreateOrderResponse, return orderId as String!
            .setCountryCode(TestingTool.DefaultTestCountryCode)	
            .setRowToCancel(1)
        ;
    	CancelOrderRowsResponse response = builder.cancelPaymentPlanOrderRows().doRequest();
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CancelOrderRowsResponse );
    }   
    // card (uses webdriver)
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

    /// WebPayAdmin.creditOrderRows() ----------------------------------------------------------------------------------	
    // invoice
    @Test
    public void test_creditOrderRows_creditInvoiceOrderRows_credit_all_rows() {
		    	
		// create an order using defaults
		CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_cancelOrderRows_cancelInvoiceOrderRows_cancel_all_rows");
		assertTrue(order.isOrderAccepted());
		
		DeliverOrderBuilder deliverBuilder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			//.addOrderRow()
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( order.orderId )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		DeliverOrdersResponse deliverResponse = deliverBuilder.deliverInvoiceOrder().doRequest();
		assertTrue(deliverResponse.isOrderAccepted());
 
		// credit first order row and assert the response
        CreditOrderRowsBuilder creditBuilder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
    		.setOrderId( order.getOrderId() )
			.setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
            .setCountryCode( COUNTRYCODE.SE )
            .setInvoiceId( deliverResponse.getInvoiceId() )
            .setRowToCredit(1)
		;
        CreditOrderRowsRequest creditRequest = creditBuilder.creditInvoiceOrderRows();
        CreditOrderRowsResponse creditResponse = creditRequest.doRequest();
        assertTrue(creditResponse.isOrderAccepted());        				
    }    
    // card (uses webdriver)
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
    		.setTransactionId( originalOrder.getTransactionId() )
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
    // direct bank (uses webdriver)
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
    		.setTransactionId( originalOrder.getTransactionId() )
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

    /// WebPayAdmin.updateOrderRows() ----------------------------------------------------------------------------------	
    // invoice
    //public void test_updateInvoiceOrderRows_updateInvoiceOrderRows_original_order_row_not_found() {
    //public void test_updateInvoiceOrderRows_updateInvoiceOrderRows_single_order_row() {    	
    //public void test_updateInvoiceOrderRows_updateInvoiceOrderRows_multiple_order_rows() {    	
	// TODO
	@Test
	public void test_updateOrderRows_updateInvoiceOrderRows_original_incvat_update_exvat_sent_as_incvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountIncVat(123.99)	// 99.99ex @24% = 123.9876 => 123.99inc
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
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(123.99), Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updateInvoiceOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(99.19), Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );
	}

	// paymentplan
    //public void test_updateOrderRows_updatePaymentPlanOrderRows_single_order_row() {    	
	// TODO
	@Test
	public void test_updateOrderRows_updatePaymentPlanOrderRows_original_exvat_update_exvat_sent_as_exvat() {

    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
				
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(999.99)	// 999.99ex @24% = 1239.9876 => 1239.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(999.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// update order row
		UpdateOrderRowsBuilder updateBuilder = WebPayAdmin.updateOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addUpdateOrderRow( 
	    		WebPayItem.numberedOrderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("credit")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
	                // NumberedOrderRow attributes:
	                .setRowNumber(1)
	                .setInvoiceId("9999999")			
	                .setCreditInvoiceId("9999999")
	                .setStatus(ORDERROWSTATUS.NOTDELIVERED)
			)
		;
		UpdateOrderRowsResponse update = updateBuilder.updatePaymentPlanOrderRows().doRequest();        
        assertTrue(update.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryUpdateBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryUpdate = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> updateOrderRows = queryUpdate.getNumberedOrderRows();
		assertEquals( 1, updateOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = updateOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 1, updateOrderRow.getRowNumber() );			
	}

    /// WebPayAdmin.addOrderRows() -------------------------------------------------------------------------------------
    // invoice
	@Test
	public void test_addOrderRows_addInvoiceOrderRows_original_exvat_added_exvat_sent_as_exvat() {

		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(99.99)	// 99.99ex @24% = 123.9876 => 123.99inc
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
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(99.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(79.99)	// 79.99ex @24% = 99.1876 => 99.19 inc
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatPercent(24)
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addInvoiceOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryInvoiceOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(79.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}		
	//paymentplan
	@Test
	public void test_addOrderRows_addPaymentPlanOrderRows_original_exvatvatpercent_added_exvatincvat_sent_as_exvat() {
    	// get payment plan params
		PaymentPlanParamsResponse paymentPlanParams = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
				.setCountryCode(TestingTool.DefaultTestCountryCode)
				.doRequest();
		String campaign = paymentPlanParams.getCampaignCodes().get(0).getCampaignCode();
			
		CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow( 
				WebPayItem.orderRow()
			        .setArticleNumber("original")
			        .setName("Prod")
			        .setDescription("Specification")
			        .setAmountExVat(1099.99)	// 1099.99ex @24% = 1363.9876 => 1363.99inc
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
		CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(campaign).doRequest();
		assertTrue(order.isOrderAccepted());		
		
		// query and assert original order row
		QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryOriginal = queryOrderBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryOriginal.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> numberedOrderRows = queryOriginal.getNumberedOrderRows();
		assertEquals( 1, numberedOrderRows.size() );		
		NumberedOrderRowBuilder orderRow = numberedOrderRows.get(0); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(orderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1099.99), Double.valueOf(orderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(orderRow.getVatPercent()) );	
		assertEquals( 1, orderRow.getRowNumber() );	

		// add order row
		AddOrderRowsBuilder addBuilder = WebPayAdmin.addOrderRows(SveaConfig.getDefaultConfig())
		    .setOrderId( String.valueOf(order.orderId) ) 
		    .setCountryCode( COUNTRYCODE.SE ) 
		    .addOrderRow( 
	    		WebPayItem.orderRow()
	    			// OrderRow attributes:
	                .setArticleNumber("addedRow")
                    .setName("Prod")
                    .setDescription("Specification")
	                .setAmountExVat(1079.99)	// 1079.99ex @24% = 1339.1876 => 1339.19 inc
	                .setAmountIncVat(1339.19)
	                .setQuantity(1.0)
	                .setUnit("st")
	                .setVatDiscount(0)
			)
		;
		AddOrderRowsResponse add = addBuilder.addPaymentPlanOrderRows().doRequest();        
        assertTrue(add.isOrderAccepted());        

		// query and assert updated order row
		QueryOrderBuilder queryAddBuilder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
		        .setOrderId(order.orderId)
		        .setCountryCode(COUNTRYCODE.SE)
		;
		GetOrdersResponse queryAdd = queryAddBuilder.queryPaymentPlanOrder().doRequest();       
		assertTrue(queryAdd.isOrderAccepted());			
		ArrayList<NumberedOrderRowBuilder> addOrderRows = queryAdd.getNumberedOrderRows();
		assertEquals( 2, addOrderRows.size() );		
		NumberedOrderRowBuilder updateOrderRow = addOrderRows.get(1); 			
		assertEquals( (Double)Double.NaN, Double.valueOf(updateOrderRow.getAmountIncVat()) );
		assertEquals( Double.valueOf(1079.99), Double.valueOf(updateOrderRow.getAmountExVat()) );
		assertEquals( Double.valueOf(24.00), Double.valueOf(updateOrderRow.getVatPercent()) );	
		assertEquals( 2, updateOrderRow.getRowNumber() );			
	}	
}

