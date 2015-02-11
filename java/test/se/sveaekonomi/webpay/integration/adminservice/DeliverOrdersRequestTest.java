package se.sveaekonomi.webpay.integration.adminservice;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import javax.xml.bind.ValidationException;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayAdmin;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DeliverOrdersRequestTest {

	/// validation	
	// invoice
    @Test
    public void test_deliverOrder_deliverInvoiceOrder_without_orderrows_return_DeliverOrdersResponse_validates_all_required_methods() {
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			//.addOrderRow()
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 123456L )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		DeliverOrdersRequest request = builder.deliverInvoiceOrder();
		assertThat( request, instanceOf(DeliverOrdersRequest.class) );
				
		try {			
			request.validateOrder();					
		}
		catch (ValidationException e){			
			// fail on validation error
	        fail();
        }		  
    }
     
    @Test
    public void test_deliverOrder_deliverInvoiceOrder_without_orderrows_return_DeliverOrdersResponse_validates_missing_methods() {
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			//.addOrderRow()
			//.setCountryCode(TestingTool.DefaultTestCountryCode)
			//.setOrderId( 123456L )
			//.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
		DeliverOrdersRequest request = builder.deliverInvoiceOrder();
		assertThat( request, instanceOf(DeliverOrdersRequest.class) );
				
		try {			
			request.validateOrder();					
			// fail if validation passes
	        fail( "Expected ValidationException missing." );
        }
		catch (ValidationException e){		
			assertEquals( 
        		"MISSING VALUE - OrderId is required, use setOrderId().\n" +
				"MISSING VALUE - distributionType is required, use setInvoiceDistributionType().\n" +
        		"MISSING VALUE - CountryCode is required, use setCountryCode().\n",
    			e.getMessage()
    		);
        }		  
    }

  	// paymentplan
	// TODO public void test_validates_all_required_methods_for_queryOrder_queryPaymentPlanOrder() {

}
