package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;


public class PayPagePaymentTest {
    
    @Test
    public void setExcludePaymentMethodsTest() {
        List<PAYMENTMETHOD> excludePaymentMethods = new ArrayList<PAYMENTMETHOD>();
        excludePaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_SE);
        excludePaymentMethods.add(PAYMENTMETHOD.DBNORDEASE);
        PayPagePayment payPagePayment = new PayPagePayment(null);
        
        payPagePayment.setExcludedPaymentMethods(excludePaymentMethods);
        
        assertTrue(PAYMENTMETHOD.SVEAINVOICEEU_SE == payPagePayment.getExcludedPaymentMethods().get(0));
        assertTrue(PAYMENTMETHOD.DBNORDEASE == payPagePayment.getExcludedPaymentMethods().get(1));
    }
    
    @Test
    public void configureExcludedPaymentMethodsTest() {
        List<PAYMENTMETHOD> excludePaymentMethods = new ArrayList<PAYMENTMETHOD>();
        excludePaymentMethods.add(PAYMENTMETHOD.KORTCERT);
        PayPagePayment payPagePayment = new PayPagePayment(null);
        payPagePayment.setPaymentMethod(PAYMENTMETHOD.SVEAINVOICEEU_SE);
        payPagePayment.setExcludedPaymentMethods(excludePaymentMethods);
        
        payPagePayment.configureExcludedPaymentMethods();

        assertTrue(PAYMENTMETHOD.SVEAINVOICEEU_SE == payPagePayment.getPaymentMethod());
        assertTrue(PAYMENTMETHOD.KORTCERT == payPagePayment.getExcludedPaymentMethods().get(0));
    }
    
    @Test
    public void excludeCardPaymentMethodsTest() {
        PayPagePayment payPagePayment = new PayPagePayment(null);
        
        payPagePayment.excludeCardPaymentMethods();
        
        assertTrue(PAYMENTMETHOD.KORTCERT == payPagePayment.getExcludedPaymentMethods().get(0));
        assertTrue(PAYMENTMETHOD.SKRILL == payPagePayment.getExcludedPaymentMethods().get(1));
    }
      
    @Test
    public void testCardPaymentOnly() {
        PayPagePayment payPagePayment = new PayPagePayment(null);
        List<PAYMENTMETHOD> includedPaymentMethods = new ArrayList<PAYMENTMETHOD>();
        includedPaymentMethods.add(PAYMENTMETHOD.KORTCERT);
        includedPaymentMethods.add(PAYMENTMETHOD.SKRILL);        
        payPagePayment.includePaymentMethods(includedPaymentMethods);
        
        assertEquals(payPagePayment.getIncludedPaymentMethods().size(), 2);
    }
    
    @Test
    public void excludeDirectPaymentMethodsTest() {
        PayPagePayment payPagePayment = new PayPagePayment(null);
        
        payPagePayment.excludeDirectPaymentMethods();
        
        assertTrue(PAYMENTMETHOD.DBNORDEASE == payPagePayment.getExcludedPaymentMethods().get(0));
        assertTrue(PAYMENTMETHOD.DBSEBSE == payPagePayment.getExcludedPaymentMethods().get(1));
        assertTrue(PAYMENTMETHOD.DBSEBFTGSE == payPagePayment.getExcludedPaymentMethods().get(2));
        assertTrue(PAYMENTMETHOD.DBSHBSE == payPagePayment.getExcludedPaymentMethods().get(3));
        assertTrue(PAYMENTMETHOD.DBSWEDBANKSE == payPagePayment.getExcludedPaymentMethods().get(4));
    }
    
    @Test
    public void includePaymentMethodsTest() {
        PayPagePayment payPagePayment = new PayPagePayment(null);
        List<PAYMENTMETHOD> includedPaymentMethods = new ArrayList<PAYMENTMETHOD>();
        includedPaymentMethods.add(PAYMENTMETHOD.KORTCERT);
        includedPaymentMethods.add(PAYMENTMETHOD.SKRILL);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICESE);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITSE);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_SE);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_SE);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_DE);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_DE);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_DK);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_DK);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_FI);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_FI);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_NL);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_NL);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEAINVOICEEU_NO);
        includedPaymentMethods.add(PAYMENTMETHOD.SVEASPLITEU_NO);
        payPagePayment.includePaymentMethods(includedPaymentMethods);
               
        assertEquals(1, payPagePayment.getExcludedPaymentMethods().size());
        assertTrue(PAYMENTMETHOD.PAYPAL == payPagePayment.getExcludedPaymentMethods().get(0));
    }
    
    @Test
    public void testBuildPayPagePaymentWithExcludepaymentMethods() throws ValidationException, Exception{  
    	List<PAYMENTMETHOD> paymentMethods = new ArrayList<PAYMENTMETHOD>();
        paymentMethods.add(PAYMENTMETHOD.SVEAINVOICESE);
        paymentMethods.add(PAYMENTMETHOD.KORTCERT);
    	
       PaymentForm form = WebPay.createOrder()
            .setTestmode()
            .addOrderRow(Item.orderRow()
                    .setArticleNumber("1")
                    .setQuantity(2)
                    .setAmountExVat(100.00)
                    .setDescription("Specification")
                    .setName("Prod")
                    .setUnit("st")
                    .setVatPercent(25)
                    .setDiscountPercent(0))  
           .addFee(Item.shippingFee()
                .setAmountExVat(50)
                .setShippingId("33")
                .setDescription("Specification")
                .setVatPercent(25))
            .addDiscount(Item.relativeDiscount()
                    .setDiscountId("1")
                    .setDiscountPercent(50)
                    .setUnit("st")
                    .setName("Relative")
                   .setDescription("RelativeDiscount"))
               .addCustomerDetails(Item.individualCustomer().setNationalIdNumber(194605092222L))
               .setCountryCode(COUNTRYCODE.SE)
               .setClientOrderNumber("33")
               .setOrderDate("2012-12-12")
               .setCurrency("SEK")
               .usePayPage()
               		.excludePaymentMethods(paymentMethods)
                    .setReturnUrl("http://myurl.se")                    
                    .getPaymentForm();

       String xml = form.getXmlMessage();
       String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--Message generated by Integration package Java--><payment><customerrefno>33</customerrefno><returnurl>http://myurl.se</returnurl><currency>SEK</currency><amount>18750</amount><vat>3750</vat><orderrows><row><sku>1</sku><name>Prod</name><description>Specification</description><amount>12500</amount><vat>2500</vat><quantity>2</quantity><unit>st</unit></row><row><sku>33</sku><name></name><description>Specification</description><amount>6250</amount><vat>1250</vat><quantity>1</quantity></row><row><sku>1</sku><name>Relative</name><description>RelativeDiscount</description><amount>-12500</amount><vat>-2500</vat><quantity>1</quantity><unit>st</unit></row></orderrows><excludepaymentmethods><exclude>SVEAINVOICESE</exclude><exclude>KORTCERT</exclude></excludepaymentmethods></payment>";
       assertEquals(expected, xml);
    
        //test values are as expected
        //currency = "SEK"
        //amount = 18750
        //vat = 3750  //may change when we recalculate in Cardpaymentclass
        //Order row 1(order): amount = 12500
        //Order row 2(shipping fee): amount = 6250
        //Order row 3(relative discount: amount = -12500
        //PaymentMethods included: KORTCERT, SVEAINVOICESE
    }
    
    @Test
    public void testpayPagePaymentExcludeCardPayments() throws ValidationException, Exception {        
        PaymentForm form = WebPay.createOrder()
            .setTestmode()
            .addOrderRow(Item.orderRow()
                    .setArticleNumber("1")
                    .setQuantity(2)
                    .setAmountExVat(100.00)
                    .setDescription("Specification")
                    .setName("Prod")
                    .setUnit("st")
                    .setVatPercent(25)
                    .setDiscountPercent(0))            
            .addDiscount(Item.relativeDiscount()
                    .setDiscountId("1")
                    .setDiscountPercent(50)
                    .setUnit("st")
                    .setName("Relative")
                    .setDescription("RelativeDiscount"))
            .addCustomerDetails(Item.individualCustomer().setNationalIdNumber(194605092222L))
                .setCountryCode(COUNTRYCODE.SE)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency("SEK")
                .usePayPage()
                	.excludeCardPaymentMethods()
                    .setReturnUrl("http://myurl.se")                    
                    .getPaymentForm();

        String xml = form.getXmlMessage();        
		String paymentMethod = xml.substring(xml.indexOf("<excludepaymentmethods><exclude>")+32, xml.indexOf("<excludepaymentmethods><exclude>")+40);      
		//check to see if the first value is one of the excluded ones
		assertEquals(PAYMENTMETHOD.KORTCERT.toString(), paymentMethod);               
    }
    
    @Test
    public void testExcludeDirectPaymentMethods() throws ValidationException, Exception {    
    PaymentForm form = WebPay.createOrder()
            .setTestmode()
            .addOrderRow(Item.orderRow()
                    .setArticleNumber("1")
                    .setQuantity(2)
                    .setAmountExVat(100.00)
                    .setDescription("Specification")
                    .setName("Prod")
                    .setUnit("st")
                    .setVatPercent(25)
                    .setDiscountPercent(0))
            
            .addDiscount(Item.relativeDiscount()
                    .setDiscountId("1")
                    .setDiscountPercent(50)
                    .setUnit("st")
                    .setName("Relative")
                    .setDescription("RelativeDiscount"))
            .addCustomerDetails(Item.individualCustomer().setNationalIdNumber(194605092222L))
                .setCountryCode(COUNTRYCODE.SE)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency("SEK")
                .usePayPage()
                	.excludeDirectPaymentMethods()
                    .setReturnUrl("http://myurl.se")                    
                    .getPaymentForm();

    
			String xml = form.getXmlMessage();        
			String paymentMethod = xml.substring(xml.indexOf("<excludepaymentmethods><exclude>")+32, xml.indexOf("<excludepaymentmethods><exclude>")+42);      
			//check to see if the first value is one of the excluded ones
			assertEquals(PAYMENTMETHOD.DBNORDEASE.toString(), paymentMethod);
    }
    
    @Test
    public void testpayPagePaymentIncludePaymentMethods() throws ValidationException, Exception {
     List<PAYMENTMETHOD> paymentMethods = new ArrayList<PAYMENTMETHOD>();
     paymentMethods.add(PAYMENTMETHOD.KORTCERT);
     paymentMethods.add(PAYMENTMETHOD.SKRILL);
        PaymentForm form = WebPay.createOrder()
            .setTestmode()
            .addOrderRow(Item.orderRow()
                    .setArticleNumber("")
                    .setQuantity(2)
                    .setAmountExVat(100.00)
                    .setDescription("Specification")
                    .setName("Prod")
                    .setUnit("st")
                    .setVatPercent(25)
                    .setDiscountPercent(0))
           
           .addDiscount(Item.relativeDiscount()
                    .setDiscountId("1")
                    .setDiscountPercent(50)
                    .setUnit("st")
                    .setName("Relative")
                    .setDescription("RelativeDiscount"))
                .addCustomerDetails(Item.individualCustomer().setNationalIdNumber(194605092222L))
                .setCountryCode(COUNTRYCODE.SE)
                .setClientOrderNumber("33")
                .setOrderDate("2012-12-12")
                .setCurrency("SEK")
                .usePayPage()                    
                    .includePaymentMethods(paymentMethods)
                    .setReturnUrl("http://myurl.se")
                    .getPaymentForm();
        
        String xml = form.getXmlMessage();        
        String paymentMethod = xml.substring(xml.indexOf("<excludepaymentmethods><exclude>")+32, xml.indexOf("<excludepaymentmethods><exclude>")+45);      
        //check to see if the first value is one of the excluded ones
        assertEquals(PAYMENTMETHOD.SVEAINVOICESE.toString(), paymentMethod);
    }
    
}
