package se.sveaekonomi.webpay.integration;

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
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.hosted.payment.PaymentMethodPayment;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.RecurTransactionResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.util.constant.SUBSCRIPTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.helper.WebServiceXmlBuilder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaSoapBuilder;

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
	
	/**
	 *  Creates an invoice order and returns the response.
	 * @return the invoice order response
	 */
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
    // deliver invoice order
	// w/o order rows
	@Test
    public void test_deliverOrder_deliverInvoiceOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = createInvoiceOrder();
        assertTrue(order.isOrderAccepted());

        Respondable response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .deliverInvoiceOrder()
                .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }
	// w/order rows
	// TODO
	
    // deliver paymentplan orders
	// w/o order rows

	// w/order rows
	
    // TODO
    // deliver card order    
    @Test
    public void test_deliverOrder_deliverCardOrder() {
    	    	
    	// create an order using defaults
    	HostedPaymentResponse order = createCardOrder();    	
    	assertTrue(order.isOrderAccepted());
    	
    	// deliver order
        Respondable response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
                .setTransactionId( order.getTransactionId() )
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .deliverCardOrder()
                .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }


	// WebPay.closeOrder() ---------------------------------------------------------------------------------------------
	// invoice
    @Test
    public void test_closeOrder_closeInvoiceOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = createInvoiceOrder();
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
    // TODO
 
    // WebPay.getPaymentPlanPricePerMonth()
    // see integration tests
    
    // WebPay.getPaymentPlanParams()
    // see integration tests
    
    // WebPay.getAddresses()
    // see integration tests
    
}
