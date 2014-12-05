package se.sveaekonomi.webpay.integration;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Hashtable;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.AnnulTransactionRequest;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.ConfirmTransactionRequest;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.CreditTransactionRequest;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.LowerTransactionRequest;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.QueryTransactionRequest;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CreditOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderRowsBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.NumberedOrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebPayAdminUnitTest {    		
	
    // WebPayAdmin.cancelOrder() -------------------------------------------------------------------------------------	
	/// returned request class
	// TODO
	/// cancelOrder validators
	// invoice
	@Test
    public void test_validates_all_required_methods_for_cancelOrder_cancelInvoiceOrder() {
    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(TestingTool.DefaultOrderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CloseOrder closeOrder = cancelOrderBuilder.cancelInvoiceOrder();            
			@SuppressWarnings("unused")
			SveaRequest<SveaCloseOrder> sveaRequest = closeOrder.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }	
	@Test
    public void test_missing_required_method_for_cancelOrder_cancelInvoiceOrder_setOrderId() {
    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                //.setOrderId(TestingTool.DefaultOrderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CloseOrder closeOrder = cancelOrderBuilder.cancelInvoiceOrder();
			@SuppressWarnings("unused")
			SveaRequest<SveaCloseOrder> sveaRequest = closeOrder.prepareRequest();

			// fail if validation passes
	        fail();
        }
		catch (SveaWebPayException e){
	        assertEquals(
        		"MISSING VALUE - OrderId is required, use setOrderId().\n", 
    			e.getCause().getMessage()
    		);	
		}
    }	    		
	@Test
    public void test_missing_required_method_for_cancelOrder_cancelInvoiceOrder_setCountryCode() {
    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(TestingTool.DefaultOrderId)
                //.setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CloseOrder closeOrder = cancelOrderBuilder.cancelInvoiceOrder();
			@SuppressWarnings("unused")
			SveaRequest<SveaCloseOrder> sveaRequest = closeOrder.prepareRequest();

			// fail if validation passes
	        fail();
        }
		catch (SveaWebPayException e){
	        assertEquals(
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n", 
    			e.getCause().getMessage()
    		);	
		}
    }			
	// paymentplan
	@Test
    public void test_validates_all_required_methods_for_cancelOrder_cancelPaymentPlanOrder() {
    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(TestingTool.DefaultOrderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CloseOrder closeOrder = cancelOrderBuilder.cancelPaymentPlanOrder();            
			@SuppressWarnings("unused")
			SveaRequest<SveaCloseOrder> sveaRequest = closeOrder.prepareRequest();					
		}
		catch (SveaWebPayException e){
			// fail on validation error
	        fail();
        }
    }		
	@Test
    public void test_missing_required_method_for_cancelOrder_cancelPaymentPlanOrder_setOrderId() {
    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                //.setOrderId(TestingTool.DefaultOrderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CloseOrder closeOrder = cancelOrderBuilder.cancelPaymentPlanOrder();
			@SuppressWarnings("unused")
			SveaRequest<SveaCloseOrder> sveaRequest = closeOrder.prepareRequest();

			// fail if validation passes
	        fail();
        }
		catch (SveaWebPayException e){
	        assertEquals(
        		"MISSING VALUE - OrderId is required, use setOrderId().\n", 
    			e.getCause().getMessage()
    		);	
		}
    }	    		
	@Test
    public void test_missing_required_method_for_cancelOrder_cancelPaymentPlanOrder_setCountryCode() {
    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(TestingTool.DefaultOrderId)
                //.setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CloseOrder closeOrder = cancelOrderBuilder.cancelPaymentPlanOrder();
			@SuppressWarnings("unused")
			SveaRequest<SveaCloseOrder> sveaRequest = closeOrder.prepareRequest();

			// fail if validation passes
	        fail();
        }
		catch (SveaWebPayException e){
	        assertEquals(
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n", 
    			e.getCause().getMessage()
    		);	
		}
    }		
	// card
	@Test
    public void test_validates_all_required_methods_for_cancelOrder_cancelCardOrder() {
    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(TestingTool.DefaultOrderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			AnnulTransactionRequest annulTransactionRequest = cancelOrderBuilder.cancelCardOrder();
			@SuppressWarnings("unused")
			Hashtable<String, String> sveaRequest = annulTransactionRequest.prepareRequest();	
		}
		catch (SveaWebPayException e){	
			// fail on validation error
	        fail("unexpected SveaWebPayException");
        }
    }	
	@Test
    public void test_missing_required_method_for_cancelOrder_cancelCardOrder_setOrderId() {
    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                //.setOrderId(TestingTool.DefaultOrderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			AnnulTransactionRequest annulTransactionRequest = cancelOrderBuilder.cancelCardOrder();
			@SuppressWarnings("unused")
			Hashtable<String, String> sveaRequest = annulTransactionRequest.prepareRequest();

			// fail if validation passes
	        fail("expected SveaWebPayException");
        }
		catch (SveaWebPayException e){
	        assertEquals(
        		"MISSING VALUE - OrderId is required, use setOrderId().\n", 
    			e.getCause().getMessage()
    		);	
		}
    }	    		
	@Test
    public void test_missing_required_method_for_cancelOrder_cancelCardOrder_setCountryCode() {
	    	    	
        CancelOrderBuilder cancelOrderBuilder = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
            .setOrderId(TestingTool.DefaultOrderId)
            //.setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			AnnulTransactionRequest annulTransactionRequest = cancelOrderBuilder.cancelCardOrder();
			@SuppressWarnings("unused")
			Hashtable<String, String> sveaRequest = annulTransactionRequest.prepareRequest();

			// fail if validation passes
	        fail("expected SveaWebPayException");
        }
		catch (SveaWebPayException e){
	        assertEquals(
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n", 
    			e.getCause().getMessage()
    		);	
		}
    }		

    /// WebPayAdmin.queryOrder() --------------------------------------------------------------------------------------------	
	/// returned request class
	// .queryInvoiceOrder => AdminService/GetOrdersRequest
	// TODO
	// .queryPaymentPlanOrder => AdminService/GetOrdersRequest
	// TODO
	// .queryDirectBankOrder => HostedService/QueryTransactionRequest
	// TODO
	// .queryCardOrder => HostedService/QueryTransactionRequest
    @Test
    public void test_queryOrder_queryCardOrder_returns_QueryTransactionRequest() {
		QueryOrderBuilder builder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
			.setTransactionId( "987654" )
			.setCountryCode( COUNTRYCODE.SE )    
		;
		QueryTransactionRequest request = builder.queryCardOrder();
		assertThat( request, instanceOf(QueryTransactionRequest.class));
	}
    
    /// builder object validation
	// invoice
	// TODO public void test_validates_all_required_methods_for_queryOrder_queryInvoiceOrder(){
	// paymentplan
	// TODO public void test_validates_all_required_methods_for_queryOrder_queryPaymentPlanOrder() {
	// directbank
	// TODO public void test_validates_all_required_methods_for_queryOrder_queryDirectBankOrder() {
	// card
    @Test
    public void test_validates_all_required_methods_for_queryOrder_queryCardOrder() {
		QueryOrderBuilder builder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
			//.setOrderId(123456L)													// invoice, partpayment only, required
		    .setTransactionId("123456")            				   					// card, direct bank only, optional -- you can also use setOrderId
			.setCountryCode(TestingTool.DefaultTestCountryCode)						// required
		;			
		try {			
			QueryTransactionRequest request = builder.queryCardOrder();						
			@SuppressWarnings("unused")
			Object soapRequest = request.prepareRequest();					
		}
		catch (SveaWebPayException e){			
			// fail on validation error
	        fail();
        }			 
    }
    @Test
    public void test_queryOrder_validates_missing_required_method_for_queryCardOrder_setOrderId() {	
		QueryOrderBuilder builder = new QueryOrderBuilder(SveaConfig.getDefaultConfig())
			//.setOrderId(123456L)													// invoice, partpayment only, required
			.setCountryCode(TestingTool.DefaultTestCountryCode)						// required
		;
		try {
			QueryTransactionRequest request = builder.queryCardOrder();						
			@SuppressWarnings("unused")
			Object soapRequest = request.prepareRequest();		
			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){			
	        assertEquals(
        		"MISSING VALUE - OrderId is required, use setOrderId().\n", 
    			e.getCause().getMessage()
    		);			
        }			
    }
    @Test
    public void test_queryOrder_validates_missing_required_method_for_queryCardOrder_setCountryCode() {	
		QueryOrderBuilder builder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
			.setOrderId(123456L)													// invoice, partpayment only, required
			//.setCountryCode(TestingTool.DefaultTestCountryCode)					// required
		;			
		try {
			QueryTransactionRequest request = builder.queryCardOrder();						
			@SuppressWarnings("unused")
			Object soapRequest = request.prepareRequest();		
			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){			
	        assertEquals(
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n", 
    			e.getCause().getMessage()
    		);			
        }			
    }

    /// WebPayAdmin.deliverOrderRows() --------------------------------------------------------------------------------------------	
	/// returned request class
	// TODO other methods
	// .deliverCardOrderRows => HostedService/ConfirmTransactionRequest
    @Test
    public void test_deliverOrderRows_deliverCardOrderRows_returns_ConfirmTransactionResponse() {
    	
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
		DeliverOrderRowsBuilder builder = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( "123456" )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToDeliver( 1 )
	    	.addNumberedOrderRows( rows )
    	;
		ConfirmTransactionRequest request = builder.deliverCardOrderRows();
		assertThat( request, instanceOf(ConfirmTransactionRequest.class));
	}    
    // builder object validation
    // TODO other methods
    // .deliverCardOrderRows validation
	@Test
    public void test_validates_all_required_methods_for_deliverOrderRows_deliverCardOrderRows() {
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    DeliverOrderRowsBuilder builder = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( "123456" )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToDeliver( 1 )
	    	.addNumberedOrderRows( rows )
    	;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			ConfirmTransactionRequest request = builder.deliverCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }		
	@Test
    public void test_deliverOrderRows_validates_missing_required_method_for_deliverCardOrderRows() { 	
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    DeliverOrderRowsBuilder builder = WebPayAdmin.deliverOrderRows(SveaConfig.getDefaultConfig())
	    	//.setOrderId( "123456" )
	    	//.setCountryCode( COUNTRYCODE.SE )
	    	//.setRowToDeliver( 1 )
	    	//.addNumberedOrderRows( rows )
    	;
		try {
			ConfirmTransactionRequest request = builder.deliverCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();			
			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){			
	        assertEquals(
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" +
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - rowIndexesToDeliver is required for deliverCardOrderRows(). Use methods setRowToDeliver() or setRowsToDeliver().\n" +
        		"MISSING VALUE - numberedOrderRows is required for deliverCardOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n",
    			e.getCause().getMessage()
    		);			
        }
    }	
    
    /// WebPayAdmin.cancelOrderRows() --------------------------------------------------------------------------------------------	
	/// returned request class
	// TODO other methods
	// .cancelCardOrderRows => HostedService/AnnulTransactionRequest
    @Test
    public void test_cancelOrderRows_cancelCardOrderRows_returns_LowerTransactionResponse() {
    	
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( "123456" )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToCancel( 1 )
	    	.addNumberedOrderRows( rows )
    	;        
	    LowerTransactionRequest request = builder.cancelCardOrderRows();            
		assertThat( request, instanceOf(LowerTransactionRequest.class));
	}    
    // builder object validation
    // TODO other methods
	// .cancelCardOrderRow validation
	@Test
    public void test_validates_all_required_methods_for_cancelOrderRows_cancelCardOrderRows() {
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( "123456" )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToCancel( 1 )
	    	.addNumberedOrderRows( rows )
    	;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			LowerTransactionRequest request = builder.cancelCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }		
	@Test
    public void test_cancelOrderRows_validates_missing_required_method_for_cancelCardOrderRows() { 	
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CancelOrderRowsBuilder builder = WebPayAdmin.cancelOrderRows(SveaConfig.getDefaultConfig())
	    	//.setOrderId( "123456" )
	    	//.setCountryCode( COUNTRYCODE.SE )
	    	//.setRowToCancel( 1 )
	    	//.addNumberedOrderRows( rows )
    	;
		try {
			LowerTransactionRequest request = builder.cancelCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();			
			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){			
	        assertEquals(
        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" +
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - rowIndexesToCancel is required for cancelCardOrderRows(). Use methods setRowToCancel() or setRowsToCancel().\n" +
        		"MISSING VALUE - numberedOrderRows is required for cancelCardOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n",
    			e.getCause().getMessage()
    		);			
        }
    }	

	/// WebPayAdmin.creditOrderRows() --------------------------------------------------------------------------------------------	
	/// returned request class
	// TODO other methods
	// .creditCardOrderRows => HostedService/CreditTransactionRequest
    @Test
    public void test_creditOrderRows_creditCardOrderRows_returns_LowerTransactionResponse() {
    	
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( "123456" )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToCredit( 1 )
	    	.addNumberedOrderRows( rows )
    	;        
	    CreditTransactionRequest request = builder.creditCardOrderRows();            
		assertThat( request, instanceOf(CreditTransactionRequest.class));
	}    
    // builder object validation
    // TODO other methods
	// .creditCardOrderRow validation
	@Test
    public void test_validates_all_required_methods_for_creditOrderRows_creditCardOrderRows_with_setRowToCredit() {
		ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
		rows.add( TestingTool.createNumberedOrderRow(1) );
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( "123456" )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	.setRowToCredit( 1 )
	    	.addNumberedOrderRows( rows )
		    //.addCreditOrderRow( customAmountRow )
    	;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CreditTransactionRequest request = builder.creditCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }		
	@Test
    public void test_validates_all_required_methods_for_creditOrderRows_creditCardOrderRows_with_addRowToCredit() {
		@SuppressWarnings("rawtypes")
		OrderRowBuilder customAmountRow = WebPayItem.orderRow()
			.setAmountExVat(100.0)
			.setVatPercent(10.0)
			.setQuantity(1.0)
		;
		
	    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
	    	.setOrderId( "123456" )
	    	.setCountryCode( COUNTRYCODE.SE )
	    	//.setRowToCredit( 1 )
	    	//.addNumberedOrderRows( rows )
		    .addCreditOrderRow( customAmountRow )
    	;
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			CreditTransactionRequest request = builder.creditCardOrderRows();            
			@SuppressWarnings("unused")
			Hashtable<String,String> hash = request.prepareRequest();					
		}
		catch (SveaWebPayException e){		
			// fail on validation error
	        fail();
        }
    }
	@Test
    public void test_creditOrderRows_validates_missing_required_method_for_creditCardOrderRows_with_setRowToCredit() { 	
			ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
			rows.add( TestingTool.createNumberedOrderRow(1) );
			
		    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
		    	//.setOrderId( "123456" )
		    	//.setCountryCode( COUNTRYCODE.SE )
		    	//.setRowToCredit( 1 )
		    	//.addNumberedOrderRows( rows )
	    	;
			try {
				CreditTransactionRequest request = builder.creditCardOrderRows();            
				@SuppressWarnings("unused")
				Hashtable<String,String> hash = request.prepareRequest();			
				// fail if validation passes
		        fail();		
			}
			catch (SveaWebPayException e){							
		        assertEquals(
	        		"MISSING VALUE - CountryCode is required, use setCountryCode(...).\n" +
	        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
	        		"MISSING VALUE - rowIndexesToCredit or newCreditOrderRows is required for creditCardOrderRows(). Use methods setRowToCredit()/setRowsToCredit() or addCreditOrderRow()/addCreditOrderRows().\n",
	        		e.getCause().getMessage()
	    		);			
	        }
	    }	
	@Test
    public void test_creditOrderRows_validates_missing_required_method_for_creditCardOrderRows_with_setRowToCredit_but_missing_NumberedOrderRows() { 				
			ArrayList<NumberedOrderRowBuilder> rows = new ArrayList<NumberedOrderRowBuilder>();
			rows.add( TestingTool.createNumberedOrderRow(1) );
			
			@SuppressWarnings("rawtypes")
			OrderRowBuilder customAmountRow = WebPayItem.orderRow()
				.setAmountExVat(100.0)
				.setVatPercent(10.0)
				.setQuantity(1.0)
			;
			
		    CreditOrderRowsBuilder builder = WebPayAdmin.creditOrderRows(SveaConfig.getDefaultConfig())
		    	.setOrderId( "123456" )
		    	.setCountryCode( COUNTRYCODE.SE )
		    	.setRowToCredit( 1 )
		    	//.addNumberedOrderRows( rows )
		    	.addCreditOrderRow( customAmountRow )
	    	;
			
			try {
				CreditTransactionRequest request = builder.creditCardOrderRows();            
				@SuppressWarnings("unused")
				Hashtable<String,String> hash = request.prepareRequest();			
				// fail if validation passes
		        fail();		
			}
			catch (SveaWebPayException e){							
		        assertEquals(
	        		"MISSING VALUE - numberedOrderRows is required for creditCardOrderRows(). Use setNumberedOrderRow() or setNumberedOrderRows().\n",
	    			e.getCause().getMessage()
	    		);			
	        }
	    }
	}
