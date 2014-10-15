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
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.response.webservice.CloseOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
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

	/// createOrder
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

        // wait for landing page to load and then parse out accepted
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("accepted")));                
        String accepted = driver.findElementById("accepted").getText();                        

        // close window
        driver.quit();

        assertEquals("true", accepted);              
	}		
	// directbank
	// TODO
	// payment method (i.e. invoice) via paypage
	// TODO
	
	/// WebPay.closeOrder
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
    
    /// WebPay.deliverOrder
    // deliver invoice order
    @Test
    public void test_deliverOrder_deliverInvoiceOrder() {
    	    	
    	// create an order using defaults
    	CreateOrderResponse order = createInvoiceOrder();
        assertTrue(order.isOrderAccepted());

        DeliverOrderResponse response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
                .setOrderId(order.orderId)
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .deliverInvoiceOrder()
                .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }
    // deliver paymentplan order
    // TODO
    // deliver card order    
    // TODO    
}
