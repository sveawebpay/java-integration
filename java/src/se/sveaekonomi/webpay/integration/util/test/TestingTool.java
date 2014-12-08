package se.sveaekonomi.webpay.integration.util.test;

import static org.junit.Assert.assertEquals;

import java.sql.Date;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.ConfigurationProvider;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.config.SveaTestConfigurationProvider;
import se.sveaekonomi.webpay.integration.exception.SveaWebPayException;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.identity.CompanyCustomer;
import se.sveaekonomi.webpay.integration.order.identity.IndividualCustomer;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.RelativeDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.response.hosted.HostedPaymentResponse;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTTYPE;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

public class TestingTool {
    public static final COUNTRYCODE DefaultTestCountryCode = COUNTRYCODE.SE;
    public static final CURRENCY DefaultTestCurrency = CURRENCY.SEK;
    public static final String DefaultTestClientOrderNumber = "33";
    public static final String DefaultTestCustomerReferenceNumber = "ref33";
    public static final String DefaultTestIndividualNationalIdNumber = "194605092222";
    public static final String DefaultTestCompanyNationalIdNumber = "164608142222";
    public static final Date DefaultTestDate = java.sql.Date.valueOf("2012-12-12");
    public static final Long DefaultOrderId = 987654L;
	
    public static OrderRowBuilder createMiniOrderRow() {
        return WebPayItem.orderRow()
                   .setQuantity(1.0)
                   .setAmountExVat(4)
                   .setVatPercent(25);
    }
    
    public static OrderRowBuilder createExVatBasedOrderRow(String articleNumber) {
    	articleNumber = articleNumber == null ? "1" : articleNumber;
        return WebPayItem.orderRow()
                .setArticleNumber(articleNumber)
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2.0)
                .setUnit("st")
                .setVatPercent(25)
                .setVatDiscount(0); 
    }
    
    public static OrderRowBuilder createPaymentPlanOrderRow() {
        return WebPayItem.orderRow()
                   .setArticleNumber("1")
                   .setName("Prod")
                   .setDescription("Specification")
                   .setAmountExVat(1000.00)
                   .setQuantity(2.0)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setVatDiscount(0);
    }
    
    public static OrderRowBuilder createIncVatBasedOrderRow(String articleNumber) {
    	articleNumber = articleNumber != null ? articleNumber : "1";
        return WebPayItem.orderRow()
                   .setArticleNumber(articleNumber)
                   .setName("Prod")
                   .setDescription("Specification")
                   .setAmountIncVat(125)
                   .setQuantity(2.0)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static OrderRowBuilder createIncAndExVatOrderRow(String articleNumber) {
    	articleNumber = articleNumber != null ? articleNumber : "1";
        return WebPayItem.orderRow()
                   .setArticleNumber(articleNumber)
                   .setName("Prod")
                   .setDescription("Specification")
                   .setAmountIncVat(125)
                   .setAmountExVat(100)
                   .setQuantity(2.0)
                   .setUnit("st")
                   .setDiscountPercent(0.0);
    }
    
    public static OrderRowBuilder createOrderRowDe() {
        return WebPayItem.orderRow()
                .setArticleNumber("1")
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2.0)
                .setUnit("st")
                .setVatPercent(19)
                .setVatDiscount(0); 
    }
    
    public static OrderRowBuilder createOrderRowNl() {
        return WebPayItem.orderRow()
                .setArticleNumber("1")
                .setName("Prod")
                .setDescription("Specification")
                .setAmountExVat(100.00)
                .setQuantity(2.0)
                .setUnit("st")
                .setVatPercent(6)
                .setVatDiscount(0); 
    }
    
    public static ShippingFeeBuilder createExVatBasedShippingFee() {
        return WebPayItem.shippingFee()
                   .setShippingId("33")
                   .setName("shipping")
                   .setDescription("Specification")
                   .setAmountExVat(50)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static ShippingFeeBuilder createIncVatBasedShippingFee() {
        return WebPayItem.shippingFee()
                   .setShippingId("33")
                   .setName("shipping")
                   .setDescription("Specification")
                   .setAmountIncVat(62.50)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static ShippingFeeBuilder createIncAndExVatShippingFee() {
        return WebPayItem.shippingFee()
                   .setShippingId("33")
                   .setName("shipping")
                   .setDescription("Specification")
                   .setAmountIncVat(62.50)
                   .setAmountExVat(50)
                   .setUnit("st")
                   .setDiscountPercent(0.0);
    }

    public static InvoiceFeeBuilder createExVatBasedInvoiceFee() {
        return WebPayItem.invoiceFee()
                   .setName("Svea fee")
                   .setDescription("Fee for invoice")
                   .setAmountExVat(50)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static InvoiceFeeBuilder createIncVatBasedInvoiceFee() {
        return WebPayItem.invoiceFee()
                   .setName("Svea fee")
                   .setDescription("Fee for invoice")
                   .setAmountIncVat(62.50)
                   .setUnit("st")
                   .setVatPercent(25)
                   .setDiscountPercent(0.0);
    }

    public static InvoiceFeeBuilder createIncAndExVatInvoiceFee() {
        return WebPayItem.invoiceFee()
                   .setName("Svea fee")
                   .setDescription("Fee for invoice")
                   .setAmountIncVat(62.50)
                   .setAmountExVat(50)
                   .setUnit("st")
                   .setDiscountPercent(0.0);
    }

    public static RelativeDiscountBuilder createRelativeDiscount() {
        return WebPayItem.relativeDiscount()
                   .setDiscountId("1")
                   .setName("Relative")
                   .setDescription("RelativeDiscount")
                   .setUnit("st")
                   .setDiscountPercent(50.0);
    }
    
    public static IndividualCustomer createIndividualCustomer() {
    	return createIndividualCustomer(null);
    }
    
    public static CompanyCustomer createCompanyCustomer() {
    	return createCompanyCustomer(null);
    }

    public static IndividualCustomer createIndividualCustomer(COUNTRYCODE country) {
        IndividualCustomer iCustomer = null;
        country = country == null ? COUNTRYCODE.SE : country; 
        
        switch (country) {
            case SE:
                iCustomer = WebPayItem.individualCustomer()
                   .setInitials("SB")
                   .setName("Tess", "Persson")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("0811111111")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Testgatan", "1")
                   .setBirthDate("19231212")
                   .setCoAddress("c/o Eriksson, Erik")
                   .setNationalIdNumber(DefaultTestIndividualNationalIdNumber)
                   .setZipCode("99999")
                   .setLocality("Stan");
                break;
            case NO:
                iCustomer = WebPayItem.individualCustomer()
                   .setName("Ola", "Normann")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("21222222")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Testveien", "2")
                   .setNationalIdNumber("17054512066")
                   .setZipCode("359")
                   .setLocality("Oslo");
                break;
            case FI:
                iCustomer = WebPayItem.individualCustomer()
                   .setName("Kukka-Maaria", "Kanerva Haapakoski")
                   .setEmail("test@svea.com")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Atomitie", "2 C")
                   .setNationalIdNumber("160264-999N")
                   .setZipCode("370")
                   .setLocality("Helsinki");
                break;
            case DK:
                iCustomer = WebPayItem.individualCustomer()
                   .setName("Hanne", "Jensen")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("22222222")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Testvejen", "42")
                   .setCoAddress("c/o Test A/S")
                   .setNationalIdNumber("2603692503")
                   .setZipCode("2100")
                   .setLocality("KØBENHVN Ø");
                break;
            case DE:
                iCustomer = WebPayItem.individualCustomer()
                   .setName("Theo", "Giebel")
                   .setEmail("test@svea.com")
                   .setIpAddress("123.123.123")
                   .setStreetAddress("Zörgiebelweg", "21")
                   .setCoAddress("c/o Test A/S")
                   .setNationalIdNumber("19680403")
                   .setZipCode("13591")
                   .setLocality("BERLIN");
                break;
            case NL:
                iCustomer = WebPayItem.individualCustomer()
                   .setInitials("SB")
                   .setName("Sneider", "Boasman")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("999999")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Gate", "42")
                   .setBirthDate("19550307")
                   .setCoAddress("138")
                   .setNationalIdNumber("19550307")
                   .setZipCode("1102 HG")
                   .setLocality("BARENDRECHT");
                break;
            default:
                throw new SveaWebPayException("Unsupported argument for method.");
        }

        return iCustomer;
    }

    public static CompanyCustomer createMiniCompanyCustomer() {
        return WebPayItem.companyCustomer()
                   .setVatNumber("2345234")
                   .setNationalIdNumber(DefaultTestCompanyNationalIdNumber)
                   .setCompanyName("TestCompagniet");
    }

    public static CompanyCustomer createCompanyCustomer(COUNTRYCODE country) {
        CompanyCustomer cCustomer = null;
        country = country == null ? COUNTRYCODE.SE : country; 

        switch (country) {
            case SE:
                cCustomer = WebPayItem.companyCustomer()
                   .setCompanyName("Tess, T Persson")
                   .setNationalIdNumber(DefaultTestCompanyNationalIdNumber)
                   .setEmail("test@svea.com")
                   .setPhoneNumber("0811111111")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testgatan", "1")
                   .setCoAddress("c/o Eriksson, Erik")
                   .setZipCode("99999")
                   .setLocality("Stan");
                break;
            case NO:
                cCustomer = WebPayItem.companyCustomer()
                   .setCompanyName("Test firma AS")
                   .setNationalIdNumber("923313850")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("22222222")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testveien", "1")
                   .setZipCode("259")
                   .setLocality("Oslo");
                break;
            case FI:
                cCustomer = WebPayItem.companyCustomer()
                   .setCompanyName("Testi Yritys Oy")
                   .setNationalIdNumber("9999999-2")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("22222222")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testitie", "1")
                   .setZipCode("370")
                   .setLocality("Helsinki");
                break;
            case DK:
                cCustomer = WebPayItem.companyCustomer()
                   .setCompanyName("Test A/S")
                   .setNationalIdNumber("99999993")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("22222222")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Testvejen", "42")
                   .setZipCode("2100")
                   .setLocality("KØBENHVN Ø");
                break;
            case DE:
                cCustomer = WebPayItem.companyCustomer()
                   .setCompanyName("K. H. Maier gmbH")
                   .setNationalIdNumber("12345")
                   .setVatNumber("DE123456789")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("0241/12 34 56")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("Adalbertsteinweg", "1")
                   .setZipCode("52070")
                   .setLocality("AACHEN");
                break;
            case NL:
                cCustomer = WebPayItem.companyCustomer()
                   .setCompanyName("Svea bakkerij 123")
                   .setVatNumber("NL123456789A12")
                   .setEmail("test@svea.com")
                   .setPhoneNumber("999999")
                   .setIpAddress("123.123.123.123")
                   .setStreetAddress("broodstraat", "1")
                   .setCoAddress("236")
                   .setZipCode("1111 CD")
                   .setLocality("BARENDRECHT");
                break;
            default:
                throw new SveaWebPayException("Unsupported argument for method.");
        }

        return cCustomer;
    }

    /**
     * returns a minimal invoice test order response from Svea using defaults values
     * @return CreateOrderResponse
     */
	public static CreateOrderResponse createInvoiceTestOrder() {
		return createInvoiceTestOrder( null );		    		    
	}    
    
	/**
	 * returns an invoice test order response from Svea using defaults values, including test name as customer reference
	 * 
	 * @param nameOfOriginatingTest
	 * @return CreateOrderResponse
	 */
	public static CreateOrderResponse createInvoiceTestOrder( String nameOfOriginatingTest ) {
        
		// create order
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
                )
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setOrderDate(TestingTool.DefaultTestDate)
    	;
        
        // include customer reference with test name if passed in
        if( nameOfOriginatingTest != null ) {
            order.setCustomerReference(nameOfOriginatingTest.substring(0, Math.min(nameOfOriginatingTest.length(),30)));
        }
        
        // break and inspect here, if needed
        @SuppressWarnings("unused")
		SveaRequest<SveaCreateOrder> soap_request = order.useInvoicePayment().prepareRequest();
        
        // choose payment method and do request
        CreateOrderResponse response = order.useInvoicePayment().doRequest();
        
        return response;
	}
    
    
	
	/**
	 * returns a payment plan test order response from Svea. 
	 * 
	 * @param nameOfOriginatingTest
	 * @return
	 */
	public static CreateOrderResponse createPaymentPlanTestOrder( String nameOfOriginatingTest ) {
        		
		// create order
        CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
        		// add order rows with sufficiently large total amount to allow payment plan to be used
                .addOrderRow(TestingTool.createPaymentPlanOrderRow())
                .addCustomerDetails(WebPayItem.individualCustomer()
                    .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
    	;

        if( nameOfOriginatingTest != null ) {
            order.setCustomerReference(nameOfOriginatingTest.substring(0, Math.min(nameOfOriginatingTest.length(),30)));
        }
        
    	// get payment plan params
        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .doRequest();
        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

        // choose payment method and do request
        SveaRequest<SveaCreateOrder> soap_request = order.usePaymentPlanPayment(code).prepareRequest(); // break and inspect here, if needed
        
        CreateOrderResponse response = order.usePaymentPlanPayment(code).doRequest();
        
        return response;
	}	

//
//	TODO check why we don't include the selenium jar in the classpath (buildpath?) for this method -- see build.xml, lib/test & lib/src??	
//	
	/**
	 * returns a card test order response from Svea 
	 * 
	 * @param nameOfOriginatingTest
	 * @return
	 */
	public static HostedPaymentResponse createCardTestOrder( String nameOfOriginatingTest ) {

		// create order
	    CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
	            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
	            .addCustomerDetails(TestingTool.createIndividualCustomer(TestingTool.DefaultTestCountryCode))
	            .setCountryCode(TestingTool.DefaultTestCountryCode)
	            //.setOrderDate(TestingTool.DefaultTestDate)
                .setClientOrderNumber("test_cancelOrder_cancelCardOrder" + Long.toString((new java.util.Date()).getTime()))
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

        // wait for landing page to load and then parse out raw response
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("accepted")));                
	    String rawresponse = driver.findElementById("rawresponse").getText();                     
	    
        // close window
        driver.quit();         
	    
        // return the parsed HostedPaymentResponse object
		ConfigurationProvider myConfig = new SveaTestConfigurationProvider();
		String mySecretWord = myConfig.getSecretWord(PAYMENTTYPE.HOSTED, DefaultTestCountryCode);		
		HostedPaymentResponse myResponse = new HostedPaymentResponse(rawresponse, mySecretWord);	    
	    return myResponse; 
	}
	
	/**
	 * returns a card test order response from Svea 
	 * 
	 * @param nameOfOriginatingTest
	 * @return
	 */
	public static HostedPaymentResponse createCardTestOrderWithThreeRows( String nameOfOriginatingTest ) {

		// create order
	    CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
	            .addOrderRow(TestingTool.createExVatBasedOrderRow("A"))
	            .addOrderRow(TestingTool.createExVatBasedOrderRow("B"))
	            .addOrderRow(TestingTool.createExVatBasedOrderRow("C"))	            
	            .addCustomerDetails(TestingTool.createIndividualCustomer(TestingTool.DefaultTestCountryCode))
	            .setCountryCode(TestingTool.DefaultTestCountryCode)
	            //.setOrderDate(TestingTool.DefaultTestDate)
                .setClientOrderNumber("test_cancelOrder_cancelCardOrder" + Long.toString((new java.util.Date()).getTime()))
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

        // wait for landing page to load and then parse out raw response
        (new WebDriverWait(driver, 10)).until(ExpectedConditions.presenceOfElementLocated(By.id("accepted")));                
	    String rawresponse = driver.findElementById("rawresponse").getText();                     
	    
        // close window
        driver.quit();         
	    
        // return the parsed HostedPaymentResponse object
		ConfigurationProvider myConfig = new SveaTestConfigurationProvider();
		String mySecretWord = myConfig.getSecretWord(PAYMENTTYPE.HOSTED, DefaultTestCountryCode);		
		HostedPaymentResponse myResponse = new HostedPaymentResponse(rawresponse, mySecretWord);	    
	    return myResponse; 
	}
}