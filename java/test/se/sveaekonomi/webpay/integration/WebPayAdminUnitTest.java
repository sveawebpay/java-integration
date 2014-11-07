package se.sveaekonomi.webpay.integration;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.Hashtable;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.AnnulTransactionRequest;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.ConfirmTransactionRequest;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebPayAdminUnitTest {    		
	
	
	
    // WebPayAdmin::cancelOrder() -------------------------------------------------------------------------------------	
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

    /// WebPay.queryOrder() --------------------------------------------------------------------------------------------	
	/// returned request class
	// .queryInvoiceOrder => AdminService/GetOrdersRequest
	// .queryPaymentPlanOrder => AdminService/GetOrdersRequest
	// .queryDirectBankOrder => HostedService/QueryTransactionRequest
	// .queryCardOrder => HostedService/QueryTransactionRequest
    @Test
    public void test_queryOrder_queryCardOrder_returns_QueryTransactionRequest() {
		QueryOrderBuilder builder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
			.setOrderId()          // required
			.setCountryCode()      // required      
		;
		Requestable request = builder.queryCardOrder();
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
//    public void test_validates_all_required_methods_for_queryOrder_queryCardOrder() {
//    }
	



}
