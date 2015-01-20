package se.sveaekonomi.webpay.integration.adminservice;

import static org.junit.Assert.*;

import javax.xml.bind.ValidationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class GetOrdersRequestTest {

	/// validation	
	// invoice
    @Test
    public void test_validates_all_required_methods_for_queryOrder_queryInvoiceOrder_using_validateOrder() {
		QueryOrderBuilder builder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
			.setOrderId(123456L)													// invoice, partpayment only, required
			.setCountryCode(TestingTool.DefaultTestCountryCode)						// required
		;			
		try {			
			GetOrdersRequest request = builder.queryInvoiceOrder();						
			request.validateOrder();					
		}
		catch (ValidationException e){			
			// fail on validation error
	        fail();
        }		  
    }
    
    @Test
    public void test_validates_all_required_methods_for_queryOrder_queryInvoiceOrder_using_prepareRequest() {
		QueryOrderBuilder builder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
			.setOrderId(123456L)													// invoice, partpayment only, required
			.setCountryCode(TestingTool.DefaultTestCountryCode)						// required
		;			
		try {
			GetOrdersRequest request = builder.queryInvoiceOrder();						
			@SuppressWarnings("unused")
			SOAPMessage sm = request.prepareRequest();
		}
		catch (SveaWebPayException e){			
			// fail on validation error
	        fail();
		} 
		catch (SOAPException e) {
			// thrown by prepareRequest
			e.printStackTrace();
		}					
    }   

    @Test
    public void test_validates_missing_methods_for_queryOrder_queryInvoiceOrder_using_validateOrder() {
		QueryOrderBuilder builder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
			//.setOrderId(123456L)													// invoice, partpayment only, required
			//.setCountryCode(TestingTool.DefaultTestCountryCode)						// required
		;			
		try {			
			GetOrdersRequest request = builder.queryInvoiceOrder();						
			request.validateOrder();					
			// fail if validation passes
	        fail( "Expected ValidationException missing." );
        }
		catch (ValidationException e){		
			assertEquals( 
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - CountryCode is required, use setCountryCode().\n",
    			e.getMessage()
    		);
        }		  
    }

    @Test
    public void test_validates__missing_methods_for_queryOrder_queryInvoiceOrder_using_prepareRequest() {
		QueryOrderBuilder builder = WebPayAdmin.queryOrder(SveaConfig.getDefaultConfig())
			//.setOrderId(123456L)													// invoice, partpayment only, required
			//.setCountryCode(TestingTool.DefaultTestCountryCode)					// required
		;			
		try {			
			GetOrdersRequest request = builder.queryInvoiceOrder();						
			@SuppressWarnings("unused")
			SOAPMessage sm = request.prepareRequest();
			// fail if validation passes
	        fail( "Expected ValidationException missing." );
        }
		catch (SveaWebPayException e){		
			assertEquals( 
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
        		"MISSING VALUE - CountryCode is required, use setCountryCode().\n",
    			e.getCause().getMessage()	// use getCause(), as SveaWebPayException wraps the ValidationException	
    		);
        }
		catch (SOAPException e) {
			// thrown by prepareRequest
			e.printStackTrace();
		}
    }    
   
  	// paymentplan
	// TODO public void test_validates_all_required_methods_for_queryOrder_queryPaymentPlanOrder() {

}
