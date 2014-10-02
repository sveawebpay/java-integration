package se.sveaekonomi.webpay.integration;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.hostedadminresponse.AnnulTransactionResponse;
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
public class WebPayAdminIntegrationTestAcceptanceTests {    
	
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
}
