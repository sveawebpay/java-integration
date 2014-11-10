package se.sveaekonomi.webpay.integration;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.handle.QueryOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.AnnulTransactionResponse;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadmin.QueryTransactionResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * contains end-to-end integration tests of WebPayAdmin entrypoints
 * used as acceptance test for package functionality, performs requests to Svea services (test environment)
 * 
 * see unit tests for validation of required entrypoint methods for the various payment methods/customers/countries
 * 
 * @author Kristian Grossman-Madsen
 */
public class WebPayAdminWebdriverTest {    
	
    /// WebPayAdmin.cancelOrder() --------------------------------------------------------------------------------------
	// invoice
    @Test
    public void test_cancelOrder_cancelInvoiceOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createInvoiceTestOrder("test_cancelOrder_cancelInvoiceOrder");
        assertTrue(order.isOrderAccepted());

        // do cancelOrder request and assert the response
        CloseOrderResponse response = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .cancelInvoiceOrder()
                	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CloseOrderResponse );
    }    
	// payment plan    
    @Test
    public void test_cancelOrder_cancelPaymentPlanOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = TestingTool.createPaymentPlanTestOrder("test_cancelOrder_cancelPaymentPlanOrder");
        assertTrue(order.isOrderAccepted());

        // do cancelOrder request and assert the response
        CloseOrderResponse response = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .cancelPaymentPlanOrder()
                	.doRequest();
        
        assertTrue(response.isOrderAccepted());        
        assertTrue(response instanceof CloseOrderResponse );
    }       
    // card
    @Test
    public void test_cancelOrder_cancelCardOrder() {
    	    	
		// create order
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                //.setOrderDate(TestingTool.DefaultTestDate)
                .setClientOrderNumber("test_cancelOrder_cancelCardOrder" + Long.toString((new Date()).getTime()))
                .setCurrency(TestingTool.DefaultTestCurrency)
        ;
                
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
        Alert alert = driver.switchTo().alert();
        alert.accept();

        // wait for landing page to load and then parse out transaction id, accepted
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("accepted")));                
        Long transactionId = Long.decode(driver.findElementById("transactionId").getText());           
        String accepted = driver.findElementById("accepted").getText();                        

        // close window
        driver.quit();

        assertEquals("true", accepted);        
        
        // do cancelOrder request and assert the response
        AnnulTransactionResponse response = null;
			response = WebPayAdmin.cancelOrder(SveaConfig.getDefaultConfig())
			        .setOrderId(transactionId)
			        .setCountryCode(TestingTool.DefaultTestCountryCode)
			        .cancelCardOrder()
			        	.doRequest();
    
        assertTrue(response.isOrderAccepted());  
        assertTrue(response instanceof AnnulTransactionResponse );
    }      

    /// WebPayAdmin.queryOrder() ---------------------------------------------------------------------------------------
    // invoice
    // TODO
    // paymentplan
    // TODO
    // card
    @Test
    public void test_queryOrder_queryCardOrder_single_order_row() {
        // created w/java package TODO make self-contained using webdriver to create card order     
        long createdOrderId = 587673L;  
        
        QueryOrderBuilder queryOrderBuilder = WebPayAdmin.queryOrder( SveaConfig.getDefaultConfig() )
            .setOrderId( createdOrderId )
            .setCountryCode( COUNTRYCODE.SE )
        ;                
        QueryTransactionResponse response = queryOrderBuilder.queryCardOrder().doRequest();         
        
        assertTrue( response.isOrderAccepted() );     
        
        // expected xml response:
		//        <?xml version="1.0" encoding="UTF-8"?><response>
		//        <transaction id="587673">
		//          <customerrefno>test_cancelOrder_cancelCardOrder1413208130564</customerrefno>
		//          <merchantid>1130</merchantid>
		//          <status>SUCCESS</status>
		//          <amount>12500</amount>
		//          <currency>SEK</currency>
		//          <vat>2500</vat>
		//          <capturedamount>12500</capturedamount>
		//          <authorizedamount>12500</authorizedamount>
		//          <created>2014-10-13 15:48:56.487</created>
		//          <creditstatus>CREDNONE</creditstatus>
		//          <creditedamount>0</creditedamount>
		//          <merchantresponsecode>0</merchantresponsecode>
		//          <paymentmethod>KORTCERT</paymentmethod>
		//          <callbackurl/>
		//          <capturedate>2014-10-14 00:15:10.287</capturedate>
		//          <subscriptionid/>
		//          <subscriptiontype/>
		//          <customer id="13662">
		//            <firstname>Tess</firstname>
		//            <lastname>Persson</lastname>
		//            <initials>SB</initials>
		//            <email>test@svea.com</email>
		//            <ssn>194605092222</ssn>
		//            <address>Testgatan</address>
		//            <address2>c/o Eriksson, Erik</address2>
		//            <city>Stan</city>
		//            <country>SE</country>
		//            <zip>99999</zip>
		//            <phone>0811111111</phone>
		//            <vatnumber/>
		//            <housenumber>1</housenumber>
		//            <companyname/>
		//            <fullname/>
		//          </customer>
		//          <orderrows>
		//            <row>
		//              <id>55950</id>
		//              <name>orderrow 1</name>
		//              <amount>12500</amount>
		//              <vat>2500</vat>
		//              <description>description 1</description>
		//              <quantity>1.0</quantity>
		//              <sku/>
		//              <unit/>
		//            </row>
		//          </orderrows>
		//        </transaction>
		//        <statuscode>0</statuscode>
		//      </response>
               
		assertEquals("587673", response.getTransactionId() );
		assertEquals("test_cancelOrder_cancelCardOrder1413208130564", response.getClientOrderNumber() );
		assertEquals("1130", response.getMerchantId());
		assertEquals("SUCCESS", response.getStatus());
		assertEquals("12500", response.getAmount());
		assertEquals("SEK", response.getCurrency());
		assertEquals("2500", response.getVat());
		assertEquals("12500", response.getCapturedAmount());
		assertEquals("12500", response.getAuthorizedAmount());									
		assertEquals("2014-10-13 15:48:56.487", response.getCreated());					
		assertEquals("CREDNONE", response.getCreditstatus());
		assertEquals("0", response.getCreditedAmount());						// TODO test
		assertEquals("0", response.getMerchantResponseCode());					
		assertEquals("KORTCERT", response.getPaymentMethod());
		assertEquals(null, response.getCallbackUrl());						// TODO test
		assertEquals("2014-10-14 00:15:10.287", response.getCaptureDate());
		assertEquals(null, response.getSubscriptionId());					// TODO test
		assertEquals(null, response.getSubscriptionType());
		assertEquals(null, response.getCardType());								
		assertEquals(null, response.getMaskedCardNumber());						
		assertEquals(null, response.getEci());										
		assertEquals(null, response.getMdstatus());
		assertEquals(null, response.getExpiryYear());
		assertEquals(null, response.getExpiryMonth());
		assertEquals(null, response.getChname());
		assertEquals(null, response.getAuthCode());			        
		
		// customer entry is ignored		
		// TODO test order rows:		
//        assertEquals("1", response.numberedOrderRows[0].rowNumber );
//        assertEquals("1.00", response.numberedOrderRows[0].quantity );
//        assertEquals("100.00", response.numberedOrderRows[0].amountExVat );
//        assertEquals("25", response.numberedOrderRows[0].vatPercent );
//        assertEquals("orderrow 1", response.numberedOrderRows[0].name );
//        assertEquals("description 1", response.numberedOrderRows[0].description );              	
    }
    // directbank
    // TODO
}
