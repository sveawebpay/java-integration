package se.sveaekonomi.webpay.integration;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import se.sveaekonomi.webpay.integration.adminservice.DeliverOrdersRequest;
import se.sveaekonomi.webpay.integration.adminservice.DeliverOrdersResponse;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.hosted.hostedadmin.ConfirmTransactionRequest;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodPayment;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.DeliverOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.ConfirmTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.RecurTransactionResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.constant.SUBSCRIPTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.handleorder.HandleOrder;

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

        HostedPaymentResponse cardOrderResponse = new HostedPaymentResponse( raw_response, SveaConfig.getDefaultConfig().getSecretWord(PAYMENTTYPE.HOSTED, COUNTRYCODE.SE) );
             
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
    	HostedPaymentResponse createCardOrderResponse = createCardOrder();    	
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
    	HostedPaymentResponse createCardOrderResponse = createCardOrder( useSetSubscriptionType );
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
		assertEquals("1000", response.getResultCode());	// probably a bug, ought to be 20004
		//assertEquals("20004", response.getResultCode());
		//assertEquals("An order with the provided id does not exist.", response.getErrorMessage());			
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
		assertEquals( String.valueOf(order.orderId), response.getOrderId() );					// TODO fix return value!	
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
    	HostedPaymentResponse order = createCardOrder();    	
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
    public void test_closeOrder_closeInvoicyeOrder() {
    	    	
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
    
}
