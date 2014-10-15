package se.sveaekonomi.webpay.integration;

import static org.junit.Assert.*;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.CloseOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.CloseOrder;
import se.sveaekonomi.webpay.integration.webservice.payment.InvoicePayment;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCloseOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class WebPayUnitTestValidation {    

	/// WebPay.createOrder
	// invoice
	@Test 
	public void test_validates_all_required_methods_for_createOrder_useInvoicePayment_IndividualCustomer_SE() {
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
	public void test_validates_missing_required_method_for_useInvoicePayment_IndividualCustomer_SE_addOrderRow() {
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
	public void test_validates_missing_required_method_for_useInvoicePayment_IndividualCustomer_SE_addCustomerDetails() {
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
	public void test_validates_missing_required_method_for_useInvoicePayment_IndividualCustomer_SE_setCountryCode() {
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
	public void test_validates_missing_required_method_for_useInvoicePayment_IndividualCustomer_SE_setOrderDate() {
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

	// TODO add validation tests for other countries customer identification, other payment methods, backport tests to php
	
//	@Test 
//	public void test_createOrder_usePaymentPlanPayment() {
//    	// create an order using defaults
//    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_createOrder_usePaymentPlanPayment");
//        assertTrue(order.isOrderAccepted());
//	}	
//
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
	
}
