package se.sveaekonomi.webpay.integration;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import se.sveaekonomi.webpay.integration.adminservice.DeliverOrdersRequest;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.ConfirmTransactionRequest;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodPayment;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.response.adminservice.DeliverOrdersResponse;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.RecurTransactionResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.GetAddressesResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.constant.SUBSCRIPTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;
import se.sveaekonomi.webpay.integration.webservice.getaddresses.GetAddresses;
import se.sveaekonomi.webpay.integration.webservice.helper.WebserviceRowFormatter;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaOrderRow;

/**
 * contains end-to-end integration tests of WebPay entrypoints
 * used as acceptance test for package functionality, performs requests to Svea services (test environment)
 * 
 * see unit tests for validation of required entrypoint methods for the various payment methods/customers/countries
 * 
 * @author Kristian Grossman-Madsen
 */
public class WebPayWebdriverTest {    

	public HostedPaymentResponse createCardOrder() {
		return createCardOrder( false );
	}
	/**
	 * Creates an order using the KORTCERT payment method and picks up the response via the CardOrder example landing page.
	 * @return the processed card order response
	 */
	public HostedPaymentResponse createCardOrder( boolean useSetSubscriptionType ) {
		// create order
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                //.setOrderDate(TestingTool.DefaultTestDate)
                .setClientOrderNumber("test_createCardOrder" + Long.toString((new Date()).getTime()))
                .setCurrency(TestingTool.DefaultTestCurrency)
        ;
                
        // choose payment method and do request
        PaymentMethodPayment request = (PaymentMethodPayment) order.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
            	.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below                	
    	;
        
        // check subscription true, setSubscriptionType!
        if( useSetSubscriptionType == true ) {
        	request.setSubscriptionType(SUBSCRIPTIONTYPE.RECURRING);
        }
        
        PaymentForm form = request.getPaymentForm();

        
        // insert form in empty page
        FirefoxDriver driver = new FirefoxDriver();
        driver.get("about:blank");
        String script = "document.body.innerHTML = '" + form.getCompleteForm() + "'";
        driver.executeScript(script);
        
        // post form
        driver.findElementById("paymentForm").submit();

        // wait for certitrade page to load
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("paymeth-list")));
        
        // fill in credentials form
        WebElement cardno = driver.findElementById("cardno");
        cardno.sendKeys("4444333322221100");       

        WebElement cvc = driver.findElementById("cvc"); 	       
    	cvc.sendKeys("123");

        Select month = new Select(driver.findElementById("month"));
        month.selectByValue("01");

        Select year = new Select(driver.findElementById("year"));
        year.selectByValue("17");
        
        // submit credentials form, triggering redirect to returnurl
        driver.findElementById("perform-payment").click();        
        
        // as our localhost landingpage is a http site, we get a popup
        Alert alert = driver.switchTo().alert();
        alert.accept();

        // wait for landing page to load and then create a HostedPaymentResponse from the response xml message
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("accepted")));                
        String raw_response = driver.findElementById("rawresponse").getText();            
        //System.out.println("raw_response:\n" + raw_response + "\n");

        String raw_response_mac = driver.findElementById("rawresponse_mac").getText();            
        //System.out.println("raw_response_mac:\n" + raw_response_mac + "\n");

        
        HostedPaymentResponse cardOrderResponse = new HostedPaymentResponse( 
        		raw_response, 
        		raw_response_mac,
        		SveaConfig.getDefaultConfig().getSecretWord(PAYMENTTYPE.HOSTED, COUNTRYCODE.SE) 
		);
             
        // close window
        driver.quit();
        
        assertTrue(cardOrderResponse.isOrderAccepted() );  	
        
        return cardOrderResponse;
	}	
	
    // WebPay.createOrder() --------------------------------------------------------------------------------------------
	// invoice
	@Test 
	public void test_createOrder_useInvoicePayment() {
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_createOrder_useInvoicePayment");
        assertTrue(order.isOrderAccepted());
	}
	// paymentplan
	@Test 
	public void test_createOrder_usePaymentPlanPayment() {
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_createOrder_usePaymentPlanPayment");
        assertTrue(order.isOrderAccepted());
	}	
	// usePaymentMethodPayment - card
	@Test 
	public void test_createOrder_usePaymentMethodPayment_KORTCERT() {          
    	// create an order using defaults
    	HostedPaymentResponse createCardOrderResponse = (HostedPaymentResponse)TestingTool.createCardTestOrder("test_createOrder_usePaymentMethodPayment_KORTCERT");
    	assertTrue(createCardOrderResponse.isOrderAccepted());
	}		
	// directbank
	// TODO
	// payment method (i.e. invoice) via paypage
	// TODO		
	
	// recur
	// set up recurring card order subscription and then perform recur order request
	@Test 
	public void test_createOrder_perform_recurring_card_order_request() {          
    	// set up subscription card order
		boolean useSetSubscriptionType = true;
    	HostedPaymentResponse createCardOrderResponse = (HostedPaymentResponse)TestingTool.createCardTestOrderWithThreeRows("test_createOrder_perform_recurring_card_order_request", useSetSubscriptionType);
    	assertTrue(createCardOrderResponse.isOrderAccepted());
    	assertNotNull( createCardOrderResponse.getSubscriptionId() );    	    	

    	// create recur order
        CreateOrderBuilder recurOrder = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            //.setOrderDate(TestingTool.DefaultTestDate)
            .setClientOrderNumber("recur" + Long.toString((new Date()).getTime()))
            .setCurrency(TestingTool.DefaultTestCurrency)
        ;
                
        // set up request and perform recur call
        PaymentMethodPayment recurRequest = (PaymentMethodPayment) recurOrder.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
        	.setReturnUrl("http://localhost:8080/CardOrder/landingpage")  
        	.setSubscriptionId( createCardOrderResponse.getSubscriptionId() )
    	;
        
        RecurTransactionResponse recurResponse = recurRequest.doRecur();
    	assertTrue(recurResponse.isOrderAccepted());    	
	}	
	
	
    // WebPay.deliverOrder() -------------------------------------------------------------------------------------------       
    // .deliverInvoiceOrder() without orderrows => AdminService/DeliverOrdersRequest request, DeliverOrdersResponse response
    @Test
    public void test_deliverOrder_deliverInvoiceOrder_without_orderrows_return_DeliverOrdersResponse_order_not_found() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			//.addOrderRow()
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 123456L )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		DeliverOrdersRequest request = builder.deliverInvoiceOrder();
		assertTrue( request instanceof Requestable );        
		assertThat( request, instanceOf(DeliverOrdersRequest.class) );
		
		DeliverOrdersResponse response = request.doRequest();
		assertThat( response, instanceOf(DeliverOrdersResponse.class) );
		assertEquals(false, response.isOrderAccepted());
		assertEquals("20004", response.getResultCode());
		assertEquals("No order found for orderId: 123456", response.getErrorMessage());			
    }
   
    @Test
    public void test_deliverOrder_deliverInvoiceOrder_without_orderrows_return_DeliverOrdersResponse_order_found() {	
    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_createOrder_useInvoicePayment");
        assertTrue(order.isOrderAccepted());
    	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			//.addOrderRow()
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( order.orderId )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		DeliverOrdersRequest request = builder.deliverInvoiceOrder();
		assertTrue( request instanceof Requestable );        
		assertThat( request, instanceOf(DeliverOrdersRequest.class) );
		
		DeliverOrdersResponse response = request.doRequest();
		assertThat( response, instanceOf(DeliverOrdersResponse.class) );
		assertEquals(true, response.isOrderAccepted());
		assertEquals( order.getOrderId(), response.getOrderId() );
    }

	// .deliverInvoiceOrder() with orderrows => WebService/HandleOrder request, DeliverOrderResponse response
    @Test
    public void test_deliverOrder_deliverInvoicePayment_with_orderrows_order_not_found() {	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					WebPayItem.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( 1234569L )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
			
		HandleOrder request = builder.deliverInvoiceOrder();
		assertTrue( request instanceof Requestable );        
		assertThat( request, instanceOf(HandleOrder.class) );
	
		DeliverOrderResponse response = request.doRequest();
		assertThat( response, instanceOf(DeliverOrderResponse.class) );
		assertEquals(false, response.isOrderAccepted());
		assertEquals("20004", response.getResultCode());
		assertEquals("An order with the provided id does not exist.", response.getErrorMessage());	
    }   
    
	// .deliverInvoiceOrder() with orderrows and setCreditInvoice
    @Test
    public void test_deliverOrder_deliverInvoicePayment_with_orderrows_and_setCreditInvoice() {	
    	
    	// create order
    	CreateOrderBuilder builder = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(WebPayItem.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
        ;
        CreateOrderResponse order = builder.useInvoicePayment().doRequest();
		assertTrue(order.isOrderAccepted());
			
		// deliver order
		DeliverOrderBuilder deliverBuilder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					WebPayItem.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( order.orderId )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
		;
		DeliverOrderResponse deliverResponse = deliverBuilder.deliverInvoiceOrder().doRequest();
		assertTrue(deliverResponse.isOrderAccepted());

		// credit order
		DeliverOrderBuilder creditBuilder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
			.addOrderRow(
					WebPayItem.orderRow()
			            .setQuantity(1.0)
			            .setAmountExVat(4)
			            .setAmountIncVat(5)
			)
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( order.orderId )
			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )
			.setCreditInvoice( String.valueOf(deliverResponse.getInvoiceId()) )
		;
		DeliverOrderResponse creditResponse = creditBuilder.deliverInvoiceOrder().doRequest();
		assertTrue(creditResponse.isOrderAccepted());		
    } 
    
    // .deliverPaymentPlanOrder() without orderrows => WebService/HandleOrder request, DeliverOrderResponse response
    @Test
    public void test_deliverOrder_deliverPaymentPlanOrder_without_orderrows_delivers_order_in_full() {
    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_deliverOrder_deliverPaymentPlanOrder_with_orderrows");
        assertTrue(order.isOrderAccepted());
        assertEquals(Double.valueOf(2500.00), (Double)order.amount);
    	
		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
            //.addOrderRow(TestingTool.createPaymentPlanOrderRow("1"))			
			.setCountryCode(TestingTool.DefaultTestCountryCode)
			.setOrderId( order.orderId )
			//.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )							
		;
			
		HandleOrder request = builder.deliverPaymentPlanOrder();
		assertTrue( request instanceof Requestable );        
		assertThat( request, instanceOf(HandleOrder.class) );
		
		DeliverOrderResponse response = request.doRequest();
		assertThat( response, instanceOf(DeliverOrderResponse.class) );
		assertEquals(true, response.isOrderAccepted());
        assertEquals(Double.valueOf(order.amount), Double.valueOf(response.getAmount()));
    }      
     
    // .deliverPaymentPlanOrder() with orderrows ought to result in validation error
    @Test
    public void test_deliverOrder_deliverPaymentPlanOrder_with_orderrows_misleadingly_delivers_order_in_full() {	        	
    		// create order with two rows
            CreateOrderBuilder orderBuilder = WebPay.createOrder(SveaConfig.getDefaultConfig())
            		// add order rows with sufficiently large total amount to allow payment plan to be used
                    .addOrderRow(TestingTool.createPaymentPlanOrderRow("1"))
                    .addOrderRow(TestingTool.createPaymentPlanOrderRow("2"))
                    .addCustomerDetails(WebPayItem.individualCustomer()
                        .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                    .setCountryCode(TestingTool.DefaultTestCountryCode)
                    .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                    .setOrderDate(TestingTool.DefaultTestDate)
                    .setCurrency(TestingTool.DefaultTestCurrency)
        	;
        	// get payment plan params and do payment plan order request
            PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
                    .setCountryCode(TestingTool.DefaultTestCountryCode)
                    .doRequest();
            String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();
            CreateOrderResponse order = orderBuilder.usePaymentPlanPayment(code).doRequest();
            assertTrue(order.isOrderAccepted());
            assertEquals(Double.valueOf(2*2500.00), (Double)order.amount);
        	
            // deliver one order row
    		DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createPaymentPlanOrderRow("1"))			// TODO should validate this is not set!
    			.setCountryCode(TestingTool.DefaultTestCountryCode)
    			.setOrderId( order.orderId )
    			.setInvoiceDistributionType( DISTRIBUTIONTYPE.Post )				// TODO should validate this is not set!			
    		;
    			
    		HandleOrder request = builder.deliverPaymentPlanOrder();
    		assertTrue( request instanceof Requestable );        
    		assertThat( request, instanceOf(HandleOrder.class) );
    		
    		DeliverOrderResponse response = request.doRequest();
    		assertThat( response, instanceOf(DeliverOrderResponse.class) );
    		assertEquals(true, response.isOrderAccepted());
            assertEquals(Double.valueOf(order.amount), Double.valueOf(response.getAmount()));
        }  
        
    // deliver card order    
    @Test
    public void test_deliverOrder_deliverCardOrder() {
    	    	
    	// create an order using defaults
    	HostedPaymentResponse order = (HostedPaymentResponse)TestingTool.createCardTestOrder("test_deliverOrder_deliverCardOrder");
    	assertTrue(order.isOrderAccepted());
    	
    	// deliver order
        DeliverOrderBuilder builder = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
	        .setTransactionId( order.getTransactionId() )
	        .setCountryCode(TestingTool.DefaultTestCountryCode)
        ;
        
	    ConfirmTransactionRequest request = builder.deliverCardOrder();
		//assertTrue( request instanceof Requestable );        
	    assertThat( request, instanceOf(ConfirmTransactionRequest.class) );
	            
	    ConfirmTransactionResponse response = request.doRequest();
		assertTrue( response instanceof Respondable );        
		assertThat( response, instanceOf(ConfirmTransactionResponse.class) );        
        assertTrue(response.isOrderAccepted());        
    }

	// WebPay.closeOrder() ---------------------------------------------------------------------------------------------
	// invoice
    @Test
    public void test_closeOrder_closeInvoiceOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder();
        assertTrue(order.isOrderAccepted());

        // test WebPay::closeOrder
        CloseOrderResponse closeResponse = WebPay.closeOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .closeInvoiceOrder()
                .doRequest();
        
        assertTrue(closeResponse.isOrderAccepted());
    }
    // paymentplan
    @Test
    public void test_closeOrder_closePaymentPlanOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_closeOrder_closePaymentPlanOrder");
        assertTrue(order.isOrderAccepted());

        // test WebPay::closeOrder
        CloseOrderResponse closeResponse = WebPay.closeOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .closePaymentPlanOrder()
                .doRequest();
        
        assertTrue(closeResponse.isOrderAccepted());
    }
 
    // WebPay.getPaymentPlanPricePerMonth()
    // see integration tests
    
    // WebPay.getPaymentPlanParams()
    // see integration tests
    
    // WebPay.getAddresses()
    // see integration tests
    
    
    // test fixed discount is sent correctly to service using invoice payment
    @Test
    public void testFormatFixedDiscountRows_sent_correctly_using_invoice_payment() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(6)
                                                                    .setQuantity(1.0))
                                                   .addFee(WebPayItem.shippingFee()	// fee row should be ignored by discount calculation
                                                		   			.setAmountExVat(50.00)
                                                		   			.setVatPercent(6))
                                                   .addFee(WebPayItem.invoiceFee()	// fee row be ignored by discount calculation
                                                		   			.setAmountExVat(23.20)
                                                		   			.setVatPercent(25))
                                		   		   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("42")
                                                                    .setName(".setAmountIncVat(100)")
                                                                    .setDescription("testFormatFixedDiscountRowsAmountIncVatWithDifferentVatRatesPresentCalculatedFromOrderItemRowsOnly")
                                                                    .setAmountIncVat(100)
                                                                    .setUnit("st"))
                                                    .addCustomerDetails(WebPayItem.individualCustomer()
                                                    				.setNationalIdNumber("4605092222")
                                    				)
                                    				.setCountryCode(COUNTRYCODE.SE)
                                    				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
        ;        									

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();

        // first order row
        SveaOrderRow newRow = newRows.get(0);
        assertEquals(100.00, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(2, newRow.NumberOfUnits, 0);
        
        // second order row 
        newRow = newRows.get(1);
        assertEquals(100.00, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);        
        
        // shipping fee row
        newRow = newRows.get(2);
        assertEquals(50.00, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        
        // invoice fee row 
        newRow = newRows.get(3);
        assertEquals(23.20, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);  
        
        // discount row @25%          
        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
        newRow = newRows.get(4);
        assertEquals(-56.18, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);

        // discount row @6%        
        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
        newRow = newRows.get(5);
        assertEquals(-28.09, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        
        // check order total        
        double total = 
        		convertExVatToIncVat( newRows.get(0).PricePerUnit, newRows.get(0).VatPercent ) * newRows.get(0).NumberOfUnits  +	// 250.00
        		convertExVatToIncVat( newRows.get(1).PricePerUnit, newRows.get(1).VatPercent ) * newRows.get(1).NumberOfUnits  +	// 106.00
        		convertExVatToIncVat( newRows.get(2).PricePerUnit, newRows.get(2).VatPercent ) * newRows.get(2).NumberOfUnits  +	// 53.00
        		convertExVatToIncVat( newRows.get(3).PricePerUnit, newRows.get(3).VatPercent ) * newRows.get(3).NumberOfUnits  +	// 29.00
        		convertExVatToIncVat( newRows.get(4).PricePerUnit, newRows.get(4).VatPercent ) * newRows.get(4).NumberOfUnits  +	// -70.22
        		convertExVatToIncVat( newRows.get(5).PricePerUnit, newRows.get(5).VatPercent ) * newRows.get(5).NumberOfUnits 		// -29.78
		;
        
        assertEquals( 338.00, total, 0.001 );
                
    	CreateOrderResponse response = order.useInvoicePayment().doRequest();
        
    	assertTrue( response.isOrderAccepted() );
    	assertEquals( 338.00, response.amount, 0.001);    	
    }
    
    // test fixed discount is sent correctly to service using card payment
    @Test
    public void testFormatFixedDiscountRows_sent_correctly_using_card_payment() {
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(25)
                                                                    .setQuantity(2.0))
                                                   .addOrderRow(WebPayItem.orderRow()
                                                                    .setAmountExVat(100.00)
                                                                    .setVatPercent(6)
                                                                    .setQuantity(1.0))
                                                   .addFee(WebPayItem.shippingFee()	// fee row should be ignored by discount calculation
                                                		   			//.setShippingId("not_a_sku")
                                                		   			.setAmountExVat(50.00)
                                                		   			.setVatPercent(6))
                                                   .addFee(WebPayItem.invoiceFee()	// fee row be ignored by discount calculation
                                                		   			.setAmountExVat(23.20)
                                                		   			.setVatPercent(25))
                                		   		   .addDiscount(WebPayItem.fixedDiscount()
                                                                    .setDiscountId("42")
                                                                    .setName(".setAmountIncVat(100)")
                                                                    .setDescription("testFormatFixedDiscountRows_sent_correctly_using_card_payment")
                                                                    .setAmountIncVat(100)
                                                                    .setUnit("st"))
                                                    .addCustomerDetails(WebPayItem.individualCustomer()
                                                    				.setNationalIdNumber("4605092222")
                                    				)
                                    				.setCountryCode(COUNTRYCODE.SE)
                                    				.setOrderDate(java.sql.Date.valueOf("2015-02-23"))
                                				    .setClientOrderNumber(Long.toString((new Date()).getTime()))
                            				    	.setCurrency(CURRENCY.SEK)
                                    				
        ;        									

        ArrayList<SveaOrderRow> newRows = new WebserviceRowFormatter(order).formatRows();

        // first order row
        SveaOrderRow newRow = newRows.get(0);
        assertEquals(100.00, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(2, newRow.NumberOfUnits, 0);
        
        // second order row 
        newRow = newRows.get(1);
        assertEquals(100.00, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);        
        
        // shipping fee row
        newRow = newRows.get(2);
        assertEquals(50.00, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        
        // invoice fee row 
        newRow = newRows.get(3);
        assertEquals(23.20, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);  
        
        // discount row @25%          
        // 100*250/356 = 70.22 incl. 25% vat => 14.04 vat as amount 
        newRow = newRows.get(4);
        assertEquals(-56.18, newRow.PricePerUnit, 0);
        assertEquals(25, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);

        // discount row @6%        
        // 100*106/356 = 29.78 incl. 6% vat => 1.69 vat as amount 
        newRow = newRows.get(5);
        assertEquals(-28.09, newRow.PricePerUnit, 0);
        assertEquals(6, newRow.VatPercent, 0);
        assertEquals(0, newRow.DiscountPercent, 0);
        assertEquals(1, newRow.NumberOfUnits, 0);
        
        // check order total        
        double total = 
        		convertExVatToIncVat( newRows.get(0).PricePerUnit, newRows.get(0).VatPercent ) * newRows.get(0).NumberOfUnits  +	// 250.00
        		convertExVatToIncVat( newRows.get(1).PricePerUnit, newRows.get(1).VatPercent ) * newRows.get(1).NumberOfUnits  +	// 106.00
        		convertExVatToIncVat( newRows.get(2).PricePerUnit, newRows.get(2).VatPercent ) * newRows.get(2).NumberOfUnits  +	// 53.00
        		convertExVatToIncVat( newRows.get(3).PricePerUnit, newRows.get(3).VatPercent ) * newRows.get(3).NumberOfUnits  +	// 29.00
        		convertExVatToIncVat( newRows.get(4).PricePerUnit, newRows.get(4).VatPercent ) * newRows.get(4).NumberOfUnits  +	// -70.22
        		convertExVatToIncVat( newRows.get(5).PricePerUnit, newRows.get(5).VatPercent ) * newRows.get(5).NumberOfUnits 		// -29.78
		;
        
        assertEquals( 338.00, total, 0.001 );
                
        // choose payment method and do request
        PaymentForm form = order.usePaymentMethod(PAYMENTMETHOD.KORTCERT)
                	.setReturnUrl("http://localhost:8080/CardOrder/landingpage")	// http => handle alert below
                	.getPaymentForm()
    	;
        
        // insert form in empty page
        FirefoxDriver driver = new FirefoxDriver();
        driver.get("about:blank");
        String script = "document.body.innerHTML = '" + form.getCompleteForm() + "'";
        driver.executeScript(script);
        
        // post form
        driver.findElementById("paymentForm").submit();

        // wait for certitrade page to load
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("paymeth-list")));
        
        // fill in credentials form
        WebElement cardno = driver.findElementById("cardno");
        cardno.sendKeys("4444333322221100");       

        WebElement cvc = driver.findElementById("cvc"); 	       
    	cvc.sendKeys("123");

        Select month = new Select(driver.findElementById("month"));
        month.selectByValue("01");

        Select year = new Select(driver.findElementById("year"));
        year.selectByValue("17");
        
        // submit credentials form, triggering redirect to returnurl
        driver.findElementById("perform-payment").click();        
        
        // as our localhost landingpage is a http site, we get a popup
		try {
	        Alert alert = driver.switchTo().alert();
	        alert.accept();
        }
        catch( NoAlertPresentException ex ) {
        	//ignore if no popup seen
        }

        // wait for landing page to load and then create a HostedPaymentResponse from the response xml message
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("accepted")));                
        String raw_response = driver.findElementById("rawresponse").getText();                        
        String mac = driver.findElementById("rawresponse_mac").getText();                        

        HostedPaymentResponse cardOrderResponse = new HostedPaymentResponse( raw_response, mac, SveaConfig.getDefaultConfig().getSecretWord(PAYMENTTYPE.HOSTED, COUNTRYCODE.SE) );
             
        // close window
        driver.quit();
        
        assertTrue(cardOrderResponse.isOrderAccepted() );  	
    	assertEquals( 338.00, cardOrderResponse.getAmount(), 0.001); 
    	// TODO looking at the actual request xml, the order is sent with this discount vat, which is incorrect:
		//<row>
		//	<sku>42</sku>
		//	<name>.setAmountIncVat(100)</name>
		//	<description>testFormatFixedDiscountRowsAmountIncVatWithDifferentVatRatesPresentCalculatedFromOrderItemRowsOnly
		//	</description>
		//	<amount>-10000</amount>
		//	<vat>-1479</vat>
		//	<quantity>1.0</quantity>
		//	<unit>st</unit>
		//</row>
    	
    	// This is due to the discount vat ratios being based on all order rows, that is, including shipping and invoice rows.
    	// the correct discount should have <vat>-1573</vat>
    	
    	
    	
    	
    	
    }
     
    private double convertExVatToIncVat(double amountExVat, double vatPercent) {
    	return amountExVat * (1+vatPercent/100);
    }

    @Test
    public void test_getAddresses_getIndividualAddresses_old_style() {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
    			.setCountryCode(COUNTRYCODE.SE)
    			.setIndividual("194605092222")
    			.setOrderTypeInvoice()
		;
    	GetAddressesResponse response = request.doRequest();
    }

    @Test
    public void test_getAddresses_getIndividualAddresses_new_style() {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
    			.setCountryCode(COUNTRYCODE.SE)
    			.setCustomerIdentifier("194605092222")
    			.getIndividualAddresses()
		;
    	GetAddressesResponse response = request.doRequest();
    
    	assertTrue( response.isOrderAccepted() );    	
    	assertTrue( response.getIndividualCustomers().get(0) instanceof IndividualCustomer );    	
    }
    
    
    @Test
    public void test_getAddresses_getCompanyAddresses_old_style() {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
    			.setCountryCode(COUNTRYCODE.SE)
    			.setCompany("194608142222")
    			.setOrderTypeInvoice()
		;
    	GetAddressesResponse response = request.doRequest();
    }
    
    @Test
    public void test_getAddresses_getCompanyAddresses_new_style() {
    	GetAddresses request = WebPay.getAddresses(SveaConfig.getDefaultConfig())
    			.setCountryCode(COUNTRYCODE.SE)
    			.setCustomerIdentifier("194608142222")
    			.getCompanyAddresses()
		;
    	GetAddressesResponse response = request.doRequest();
    
    	assertTrue( response.isOrderAccepted() );    	
    	assertTrue( response.getCompanyCustomers().get(0) instanceof CompanyCustomer );    	
    }
    
//    @Test
//    public void test_getAddresses_getCompanyAddresses() {
//    	
//    }

}
