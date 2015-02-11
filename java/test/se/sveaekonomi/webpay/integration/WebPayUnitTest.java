package se.sveaekonomi.webpay.integration;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.adminservice.DeliverOrdersRequest;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.ConfirmTransactionRequest;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.payment.InvoicePayment;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;
import se.sveaekonomi.webpay.integration.Requestable;

public class WebPayUnitTest {    

	public CreateOrderResponse createInvoiceOrder() {

		SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .useInvoicePayment()
                .prepareRequest();
        
        WebServiceXmlBuilder xmlBuilder = new WebServiceXmlBuilder();
        
        String xml = xmlBuilder.getCreateOrderEuXml(request.request);
        String url = SveaConfig.getTestWebserviceUrl().toString();
        String soapMessage = soapBuilder.makeSoapMessage("CreateOrderEu", xml);
        NodeList soapResponse = soapBuilder.createOrderEuRequest(soapMessage, url);
        CreateOrderResponse response = new CreateOrderResponse(soapResponse);
        
        return response;
	}	

    // WebPay.createOrder() --------------------------------------------------------------------------------------------	
	/// returned request class
	// TODO
	/// validation
	// invoice
	@Test 
	public void test_createOrder_validates_all_required_methods_for_useInvoicePayment_IndividualCustomer_SE() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					WebPayItem.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.addCustomerDetails(
					WebPayItem.individualCustomer()
			        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(COUNTRYCODE.SE)
			.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
			@SuppressWarnings("unused")
			SveaRequest<SveaCreateOrder> sveaRequest = invoicePayment.prepareRequest();					
		}
		catch (SveaWebPayException e){
			
			// fail on validation error
	        fail();
        }	
	}				
	@Test
	public void test_createOrder_validates_missing_required_method_for_useInvoicePayment_IndividualCustomer_SE_addOrderRow() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			//.addOrderRow(
			//		Item.orderRow()
			//            .setQuantity(1.0)
			//            .setAmountExVat(4)
			//            .setAmountIncVat(5)
			//)
			.addCustomerDetails(
					WebPayItem.individualCustomer()
			        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(COUNTRYCODE.SE)
			.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
			@SuppressWarnings("unused")
			SveaRequest<SveaCreateOrder> sveaRequest = invoicePayment.prepareRequest();					

			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){			
	        assertEquals(
        		"MISSING VALUE - OrderRows are required. Use addOrderRow(Item.orderRow) to get orderrow setters.\n", 
    			e.getCause().getMessage()
    		);			
        }	
	}    
	@Test
	public void test_createOrder_validates_missing_required_method_for_useInvoicePayment_IndividualCustomer_SE_addCustomerDetails() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					WebPayItem.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			//.addCustomerDetails(
			//		Item.individualCustomer()
			//        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			//)
			.setCountryCode(COUNTRYCODE.SE)
			.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
			@SuppressWarnings("unused")
			SveaRequest<SveaCreateOrder> sveaRequest = invoicePayment.prepareRequest();					

			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){			
	        assertEquals(
        		"MISSING VALUE - CustomerIdentity must be set.\nMISSING VALUE - CustomerIdentity must be set.\n", 
    			e.getCause().getMessage()
    		);			
        }	
	}
	@Test
	public void test_createOrder_validates_missing_required_method_for_useInvoicePayment_IndividualCustomer_SE_setCountryCode() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					WebPayItem.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.addCustomerDetails(
					WebPayItem.individualCustomer()
			        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			//.setCountryCode(COUNTRYCODE.SE)
			.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
			@SuppressWarnings("unused")
			SveaRequest<SveaCreateOrder> sveaRequest = invoicePayment.prepareRequest();					

			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){			
	        assertEquals(
        		"MISSING VALUE - CountryCode is required. Use setCountryCode().\n", 
    			e.getCause().getMessage()
    		);			
        }	
	}
	@Test
	public void test_createOrder_validates_missing_required_method_for_useInvoicePayment_IndividualCustomer_SE_setOrderDate() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					WebPayItem.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.addCustomerDetails(
					WebPayItem.individualCustomer()
			        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(COUNTRYCODE.SE)
			//.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
			@SuppressWarnings("unused")
			SveaRequest<SveaCreateOrder> sveaRequest = invoicePayment.prepareRequest();					

			// fail if validation passes
	        fail();		
		}
		catch (SveaWebPayException e){			
	        assertEquals(
        		"MISSING VALUE - OrderDate is required. Use setOrderDate().\n", 
    			e.getCause().getMessage()
    		);			
        }	
	}
	// TODO as above for all validation tests of create order -- countries, customers, other payment methods et al
	// public class WebServiceOrderValidatorTest {
	// public class HostedOrderValidatorTest {

    // WebPay.deliverOrder() -------------------------------------------------------------------------------------------	
	/// returned request class
	// .deliverInvoiceOrder() with orderrows => WebService/HandleOrder request (DeliverInvoice?)
    @Test
    public void test_deliverOrder_deliverInvoicePayment_with_orderrows_return_HandleOrder() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					WebPayItem.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 123456L )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		HandleOrder request = builder.deliverInvoiceOrder();
		assertThat( request, instanceOf(HandleOrder.class) );
    }
    // .deliverInvoiceOrder() without orderrows => AdminService/DeliverOrdersRequest
    @Test
    public void test_deliverOrder_deliverInvoiceOrder_without_orderrows_return_DeliverOrdersRequest() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 123456L )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		DeliverOrdersRequest request = builder.deliverInvoiceOrder();
		assertThat( request, instanceOf(DeliverOrdersRequest.class) );
    }
	// .deliverPaymentPlanOrder() with orderrows => validation error + other validation tests
    // TODO
    // .deliverPaymentPlanOrder() without orderrows => AdminService/DeliverOrdersRequest
    @Test
    public void test_deliverOrder_deliverPaymentPlanOrder_returns_DeliverOrdersRequest() {
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 123456L )
		;
			
		DeliverOrdersRequest request = builder.deliverPaymentPlanOrder();
		assertThat( request, instanceOf(DeliverOrdersRequest.class) );
    }
	// .deliverCardOrder => HostedService/ConfirmTransactionRequest	
    @Test
    public void test_deliverOrder_deliverCardOrder_returns_ConfirmTransactionRequest() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 123456L )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;			
		ConfirmTransactionRequest request = builder.deliverCardOrder();
		assertThat( request, instanceOf(ConfirmTransactionRequest.class) );
    }
    
	/// validation	
    // .deliverInvoiceOrder() with orderrows
    // TODO
    // deliverInvoiceOrder() without orderrows
    // TODO    
    // .deliverPaymentPlanOrder() with orderrows
	// TODO
    // .deliverPaymentPlanOrder() without orderrows
	// TODO
    // .deliverCardOrder()
    @Test
    public void test_deliverOrder_validates_all_required_methods_for_deliverCardOrder() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			//.setOrderId(123456L)													// invoice, partpayment only, required
		    .setTransactionId("123456")            				   					// card only, optional -- you can also use setOrderId
			.setCountryCode(TestingTool.DefaultTestCountryCode)						// required
		    .setCaptureDate( String.format("%tFT%<tRZ", new Date()) )              	// card only, optional
		;			

		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {			
			ConfirmTransactionRequest request = builder.deliverCardOrder();						
			//SveaRequest<SveaCreateOrder> sveaRequest = invoicePayment.prepareRequest();					
			@SuppressWarnings("unused")
			Object soapRequest = request.prepareRequest();					
		}
		catch (SveaWebPayException e){			
			// fail on validation error
	        fail();
        }			
    }
    @Test
    public void test_createOrder_validates_missing_required_method_for_deliverCardOrder_setOrderId() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			//.setOrderId(123456L)													// invoice, partpayment only, required
			.setCountryCode(TestingTool.DefaultTestCountryCode)						// required
		;			
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			ConfirmTransactionRequest request = builder.deliverCardOrder();						
			//SveaRequest<SveaCreateOrder> sveaRequest = invoicePayment.prepareRequest();					
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
    public void test_createOrder_validates_missing_required_method_for_deliverCardOrder_setCountryCode() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setOrderId(123456L)													// invoice, partpayment only, required
			//.setCountryCode(TestingTool.DefaultTestCountryCode)					// required
		;			
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			ConfirmTransactionRequest request = builder.deliverCardOrder();						
			//SveaRequest<SveaCreateOrder> sveaRequest = invoicePayment.prepareRequest();					
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
}
