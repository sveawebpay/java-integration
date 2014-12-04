package se.sveaekonomi.webpay.integration;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.AnnulTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.HostedAdminResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.LowerTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.QueryTransactionResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
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
    // invoice
    // TODO
    // paymentplan
    // TODO
    // card
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
    	
    	// create an order using defaults
    	HostedPaymentResponse order = TestingTool.createCardTestOrder("test_deliverOrderRows_deliverCardOrderRows_deliver_entire_order");
        assertTrue(order.isOrderAccepted());

        // do deliverOrderRows request and assert the response
        
        // first, queryOrder to get original order rows
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
        
		// then select the corresponding request class and send request
        ConfirmTransactionResponse response = deliverRequest.deliverCardOrderRows().doRequest();

        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof ConfirmTransactionResponse );    	
    }
    
    @Test
    public void test_deliverOrderRows_deliverCardOrderRows_deliver_first_row_of_three() {
    	
    	// create an order using defaults
    	HostedPaymentResponse order = TestingTool.createCardTestOrderWithThreeRows("test_deliverOrderRows_deliverCardOrderRows_deliver_first_row_of_three");
        assertTrue(order.isOrderAccepted());

        // do deliverOrderRows request and assert the response
        
        // first, queryOrder to get original order rows
        QueryOrderBuilder queryOriginalOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(order.getTransactionId())
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse originalOrder = queryOriginalOrder.queryCardOrder().doRequest();         
        
        assertTrue( originalOrder.isOrderAccepted() );             
        assertEquals( 1, originalOrder.getNumberedOrderRows().get(0).getRowNumber() );

        DeliverOrderRowsBuilder deliverRequest = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( originalOrder.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .setRowToDeliver(1)
		    .addNumberedOrderRows(originalOrder.getNumberedOrderRows()) 
		    //.addOrderRow()					// optional, add new order row to deliver along with indexed rows 	// TODO backport to php
		;
        
		// then select the corresponding request class and send request
        ConfirmTransactionResponse response = deliverRequest.deliverCardOrderRows().doRequest();

        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof ConfirmTransactionResponse );    	

        // check amounts in deliveredOrder
        QueryOrderBuilder queryDeliveredOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( order.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse deliveredOrder = queryDeliveredOrder.queryCardOrder().doRequest();         
        
        assertTrue( deliveredOrder.isOrderAccepted() );        
		assertEquals("75000", deliveredOrder.getAmount());	
		assertEquals("25000", deliveredOrder.getAuthorizedAmount());    
    }
    
    @Test
    public void test_deliverOrderRows_deliverCardOrderRows_deliver_first_and_second_row_of_three() {
    	
    	// create an order using defaults
    	HostedPaymentResponse order = TestingTool.createCardTestOrderWithThreeRows("test_deliverOrderRows_deliverCardOrderRows_deliver_first_and_second_row_of_three");
        assertTrue(order.isOrderAccepted());

        // do deliverOrderRows request and assert the response
        
        // first, queryOrder to get original order rows
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
		    //.addOrderRow()					// optional, add new order row to deliver along with indexed rows 	// TODO backport to php
		;
        
		// then select the corresponding request class and send request
        ConfirmTransactionResponse response = deliverRequest.deliverCardOrderRows().doRequest();

        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof ConfirmTransactionResponse );    	

        // check amounts in deliveredOrder
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
    public void test_deliverOrderRows_deliverCardOrderRows_cancel_all_rows() {
    	
    	// create an order using defaults
    	HostedPaymentResponse order = TestingTool.createCardTestOrder("test_deliverOrderRows_deliverCardOrderRows_cancel_all_rows");
        assertTrue(order.isOrderAccepted());

        // do cancelOrderRows request and assert the response
        
        // first, queryOrder to get original order rows
        QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId(order.getTransactionId())
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse queryResponse = queryOrderBuilder.queryCardOrder().doRequest();         
        
        assertTrue( queryResponse.isOrderAccepted() );             
        assertEquals( 1, queryResponse.getNumberedOrderRows().get(0).getRowNumber() );

        CancelOrderRowsBuilder cancelRequest = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
    		.setTransactionId( queryResponse.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
		    .setRowToCancel(1)
		    .addNumberedOrderRows(queryResponse.getNumberedOrderRows()) 
		;
        
		// then select the corresponding request class and send request
        LowerTransactionResponse response = cancelRequest.cancelCardOrderRows().doRequest();

        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof LowerTransactionResponse );    	
    }

    @Test
    public void test_cancelOrderRows_cancelCardOrderRows_cancel_first_and_second_row_of_three() {
    	
    	// create an order using defaults
    	HostedPaymentResponse order = TestingTool.createCardTestOrderWithThreeRows("test_deliverOrderRows_deliverCardOrderRows_deliver_first_and_second_row_of_three");
        assertTrue(order.isOrderAccepted());

        // do deliverOrderRows request and assert the response
        
        // first, queryOrder to get original order rows
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
		    //.addOrderRow()					// optional, add new order row to deliver along with indexed rows 	// TODO backport to php
		;
        
		// then select the corresponding request class and send request
        ConfirmTransactionResponse response = deliverRequest.deliverCardOrderRows().doRequest();

        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof ConfirmTransactionResponse );    	

        // check amounts in deliveredOrder
        QueryOrderBuilder queryDeliveredOrder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setTransactionId( order.getTransactionId() )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse deliveredOrder = queryDeliveredOrder.queryCardOrder().doRequest();         
        
        assertTrue( deliveredOrder.isOrderAccepted() );        
		assertEquals("75000", deliveredOrder.getAmount());	
		assertEquals("50000", deliveredOrder.getAuthorizedAmount());    
    }    
    
//    /**
//     * test_manual_CancelOrderBuilderRows_Card_single_row_success_step_1
//     *
//     */
//    function test_manual_CancelOrderBuilderRows_Card_single_row_success_step_1() {
//        
//        // Stop here and mark this test as incomplete.
//        $this->markTestIncomplete(
//            'skeleton for test_manual_CancelOrderBuilderRows_Card_single_row_success_step_1, step 1'
//        );
//          
//        // 1. remove (put in a comment) the above code to enable the test
//        // 2. run the test, and get the paymenturl from the output
//        // 3. go to the paymenturl and complete the transaction manually, making note of the response transactionid
//        // 4. enter the transactionid into test_manual_queryOrder_queryCard_order_step_2() below and run the test
//        
//        $orderLanguage = "sv";   
//        $returnUrl = "https://test.sveaekonomi.se/webpay-admin/admin/merchantresponsetest.xhtml";
//        $ipAddress = "127.0.0.1";
//        
//        // create order w/three rows
//        $order = \TestUtil::createOrderWithoutOrderRows( TestUtil::createIndividualCustomer("SE")->setIpAddress($ipAddress) );       
//
//        // 2x100 @25 = 25000 (5000)
//        // amount = 25000, vat = 5000
//        $country = "SE";
//
//        $order
//            ->addOrderRow( WebPayItem::orderRow()
//                ->setQuantity(2)
//                ->setAmountExVat(100.00)
//                ->setVatPercent(25)
//            )
//        ;
//            
//        $response = $order
//            ->setClientOrderNumber("foo".date('c'))
//            ->usePayPageCardOnly()
//            ->setPayPageLanguage($orderLanguage)
//            ->setReturnUrl($returnUrl)
//            ->getPaymentUrl();       
//            
//        // check that request was accepted
//        $this->assertEquals( 1, $response->accepted );                
//
//        // print the url to use to confirm the transaction
//        print_r( "\n test_manual_CancelOrderBuilderRows_Card_single_row_success_step_1(): " . $response->testurl ."\n ");
//    }
//
//    /**
//     * test_manual_CancelOrderBuilderRows_Card_single_row_success_step_2
//     * 
//     * run this test manually after you've performed a direct bank transaction and have gotten the transaction details needed
//     */  
//    function test_manual_CancelOrderBuilderRows_Card_single_row_success_step_2() {
//        
//        // Stop here and mark this test as incomplete.
//        $this->markTestIncomplete(
//            'test_manual_CancelOrderBuilderRows_Card_single_row_success_step_2, step 2'
//        );
//        
//        // 1. remove (put in a comment) the above code to enable the test
//        // 2. set $createdOrderId to the transactionid from the transaction log of the request done by following the url from step 1 above.
//        // 3. below is an example of the xml generated by paypage for the request in step 1 above, along with the transaction id, for reference.
//             
//        $createdOrderId = 590634;
//        $country = "SE";
//
//        // query orderrows
//        $queryOrderBuilder = WebPayAdmin::queryOrder( Svea\SveaConfig::getDefaultConfig() )
//            ->setOrderId( $createdOrderId )
//            ->setCountryCode($country)
//        ;     
//        $queryResponse = $queryOrderBuilder->queryCardOrder()->doRequest(); 
//
//        //print_r( $queryResponse);
//        // 2x100 @25 = 25000 (5000)
//        // amount = 25000, vat = 5000
//        $this->assertEquals(1, $queryResponse->accepted);
//        $this->assertEquals(25000, $queryResponse->amount);
//        $this->assertEquals(5000,  $queryResponse->vat);
//        $this->assertEquals(25000, $queryResponse->authorizedamount);
//
//        // cancel first order row
//        $cancelBuilder = new \Svea\CancelOrderRowsBuilder( Svea\SveaConfig::getDefaultConfig() );
//        $cancelOrderRowsBuilder = $cancelBuilder
//            ->setOrderId( $createdOrderId )
//            ->setCountryCode($country)
//            ->setRowToCancel(1)
//            ->addNumberedOrderRows($queryResponse->numberedOrderRows)
//        ;
//        $cancelOrderRowsResponse = $cancelOrderRowsBuilder->cancelCardOrderRows()->doRequest();       
//        $this->assertEquals(1, $cancelOrderRowsResponse->accepted);
//
//        // query orderrows
//        $queryOrderBuilder = WebPayAdmin::queryOrder( Svea\SveaConfig::getDefaultConfig() )
//            ->setOrderId( $createdOrderId )
//            ->setCountryCode($country)
//        ;
//                
//        $query2Response = $queryOrderBuilder->queryCardOrder()->doRequest(); 
//        
//        //print_r( $query2Response);
//        // 2x100 @25 = 25000 (5000)     <- credited
//        // amount = 25000 -25000, vat = 5000 
//        $this->assertEquals(1, $query2Response->accepted);
//        $this->assertEquals(1, $query2Response->accepted);
//        $this->assertEquals(25000, $query2Response->amount);
//        $this->assertEquals(5000,  $query2Response->vat);
//        $this->assertEquals(00000, $query2Response->authorizedamount);
//        $this->assertEquals("ANNULLED", $query2Response->status);
//    }      

}
