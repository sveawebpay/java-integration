package se.sveaekonomi.webpay.integration;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import org.junit.Test;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.adminservice.request.DeliverOrdersRequest;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.ConfirmTransactionRequest;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.payment.InvoicePayment;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaDeliverOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;
import se.sveaekonomi.webpay.integration.Requestable;

public class WebPayUnitTestValidation {    

	public CreateOrderResponse createInvoiceOrder() {

		SveaSoapBuilder soapBuilder = new SveaSoapBuilder();
        
        SveaRequest<SveaCreateOrder> request = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(Item.individualCustomer()
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
	// invoice
	@Test 
	public void test_createOrder_validates_all_required_methods_for_useInvoicePayment_IndividualCustomer_SE() {
		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					Item.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.addCustomerDetails(
					Item.individualCustomer()
			        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(COUNTRYCODE.SE)
			.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
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
					Item.individualCustomer()
			        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(COUNTRYCODE.SE)
			.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
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
					Item.orderRow()
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
					Item.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.addCustomerDetails(
					Item.individualCustomer()
			        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			//.setCountryCode(COUNTRYCODE.SE)
			.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
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
					Item.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.addCustomerDetails(
					Item.individualCustomer()
			        	.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
			)
			.setCountryCode(COUNTRYCODE.SE)
			//.setOrderDate(TestingTool.DefaultTestDate)
		;
		
		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
		try {
			InvoicePayment invoicePayment = order.useInvoicePayment();
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
	// for all validation tests of create order -- countries, customers, other payment methods et al, see
	// public class WebServiceOrderValidatorTest {
	// public class HostedOrderValidatorTest {

//	@Test 
//	public void test_createOrder_usePaymentMethodPayment_KORTCERT() {
//		// create order
//        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
//                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
//                .addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
//                .setCountryCode(TestingTool.DefaultTestCountryCode)
//                //.setOrderDate(TestingTool.DefaultTestDate)
//                .setClientOrderNumber("test_cancelOrder_cancelCardOrder" + Long.toString((new Date()).getTime()))
//                .setCurrency(TestingTool.DefaultTestCurrency)
//        ;
//                
//        // choose payment method and do request
//        PaymentForm form = order.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
//                	.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
//                	.getPaymentForm()
//    	;
//        
//        // insert form in empty page
//        FirefoxDriver driver = new FirefoxDriver();
//        driver.get("about:blank");
//        String script = "document.body.innerHTML = '" + form.getCompleteForm() + "'";
//        driver.executeScript(script);
//        
//        // post form
//        driver.findElementById("paymentForm").submit();
//
//        // wait for certitrade page to load
//        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("paymeth-list")));
//        
//        // fill in credentials form
//        WebElement cardno = driver.findElementById("cardno");
//        cardno.sendKeys("4444333322221100");       
//
//        WebElement cvc = driver.findElementById("cvc"); 	       
//    	cvc.sendKeys("123");
//
//        Select month = new Select(driver.findElementById("month"));
//        month.selectByValue("01");
//
//        Select year = new Select(driver.findElementById("year"));
//        year.selectByValue("17");
//        
//        // submit credentials form, triggering redirect to returnurl
//        driver.findElementById("perform-payment").click();        
//        
//        // as our localhost landingpage is a http site, we get a popup
//        Alert alert = driver.switchTo().alert();
//        alert.accept();
//
//        // wait for landing page to load and then parse out transaction id
//        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("accepted")));
//                
//        String accepted = driver.findElementById("accepted").getText();                        
//        assertEquals("true", accepted);        
//	}		
	

    // WebPay.deliverOrder() -------------------------------------------------------------------------------------------
	
	/// test deliverInvoiceOrder() request class type
	// .deliverInvoiceOrder() with orderrows => WebService/HandleOrder request (DeliverInvoice?)
    @Test
    public void test_deliverOrder_deliverInvoicePayment_with_orderrows_return_HandleOrder() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					Item.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 123456L )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		Requestable request = builder.deliverInvoiceOrder();
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
			
		Requestable request = builder.deliverInvoiceOrder();
		assertThat( request, instanceOf(DeliverOrdersRequest.class) );
    }
    
	// TODO .deliverInvoiceOrder() with orderrows => validation error
	// TODO .deliverPaymentPlanOrder() without orderrows => AdminService/DeliverOrdersRequest
    
	// .deliverCardOrder => HostedService/ConfirmTransactionRequest	
    @Test
    public void test_deliverOrder_deliverCardOrder_returns_ConfirmTransactionRequest() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 123456L )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		Requestable request = builder.deliverCardOrder();
		assertThat( request, instanceOf(ConfirmTransactionRequest.class) );
    }
    
	/// validation of required order builder methods
	
    // all required methods	
//    @Test
//    public void test_deliverOrder_validates_all_required_methods_for_deliverInvoicePayment() {
//    	
//		DeliverOrderBuilder order = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
//            .setCountryCode(TestingTool.DefaultTestCountryCode)
//			.setOrderId( 123456L )
//			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
//		;
//			
//		// prepareRequest() validates the order and throws SveaWebPayException on validation failure
//		try {
//			HandleOrder invoiceOrder = order.deliverInvoiceOrder();
//			SveaRequest<SveaDeliverOrder> sveaRequest = invoiceOrder.prepareRequest();					
//		}
//		catch (SveaWebPayException e){
//			// fail on validation error
//	        fail(e.getCause().getMessage());
//        }	
//    }
    
    // validate throws validation exception on missing required methods
    // TODO
    
    // deliver paymentplan order
	// TODO

    // deliver card order    
	// TODO
}
