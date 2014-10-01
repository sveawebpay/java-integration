package se.sveaekonomi.webpay.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.CancelOrderBuilder;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebPayAdminUnitTestValidation {    		
	
	
	// validate required methods for invoice orders below:

	
	@Test
    public void test_validates_all_required_methods_for_cancelOrder_cancelInvoiceOrder_() {
    	    	
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
	
	
	// validate required methods for payment plan orders below:

	@Test
    public void test_validates_all_required_methods_for_cancelOrder_cancelPaymentPlanOrder_() {
    	    	
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
	
	
}
