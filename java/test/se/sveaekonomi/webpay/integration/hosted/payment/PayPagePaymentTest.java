package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.INVOICETYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTPLANTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class PayPagePaymentTest {

    @Test
    public void setExcludePaymentMethodsTest() {
        List<PAYMENTMETHOD> excludePaymentMethods = new ArrayList<PAYMENTMETHOD>();
        excludePaymentMethods.add(PAYMENTMETHOD.INVOICE);
        excludePaymentMethods.add(PAYMENTMETHOD.NORDEA_SE);
        
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .usePayPage()
            .excludePaymentMethods(excludePaymentMethods);
        
        assertEquals(8, payPagePayment.getExcludedPaymentMethods().size());
        assertEquals(INVOICETYPE.INVOICESE.getValue(), payPagePayment.getExcludedPaymentMethods().get(0));
        assertEquals(INVOICETYPE.INVOICE_SE.getValue(), payPagePayment.getExcludedPaymentMethods().get(1));
        assertEquals(INVOICETYPE.INVOICE_NO.getValue(), payPagePayment.getExcludedPaymentMethods().get(2));
        assertEquals(INVOICETYPE.INVOICE_DK.getValue(), payPagePayment.getExcludedPaymentMethods().get(3));
        assertEquals(INVOICETYPE.INVOICE_FI.getValue(), payPagePayment.getExcludedPaymentMethods().get(4));
        assertEquals(INVOICETYPE.INVOICE_NL.getValue(), payPagePayment.getExcludedPaymentMethods().get(5));
        assertEquals(INVOICETYPE.INVOICE_DE.getValue(), payPagePayment.getExcludedPaymentMethods().get(6));
        assertEquals(PAYMENTMETHOD.NORDEA_SE.getValue(), payPagePayment.getExcludedPaymentMethods().get(7));
    }
    
    @Test
    public void setExcludePaymentMethodsTestDefaultConfigurationNoExcluded() {
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .usePayPage()
                .excludePaymentMethods();
        payPagePayment.includePaymentMethods();
        
        assertEquals(23, payPagePayment.getExcludedPaymentMethods().size());
        assertEquals(0, payPagePayment.getIncludedPaymentMethods().size());
    }
    
    @Test
    public void testDefaultSE() {
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .usePayPage();
        payPagePayment.configureExcludedPaymentMethods();
        
        assertEquals(0, payPagePayment.getExcludedPaymentMethods().size());
    }
    
    @Test
    public void setPaymentMethodTestSE() {
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .usePayPage()
            .setPaymentMethod(PAYMENTMETHOD.INVOICE);
        payPagePayment.configureExcludedPaymentMethods();
        
        assertEquals(INVOICETYPE.INVOICE_SE.getValue(), payPagePayment.getPaymentMethod());
    }
    
    @Test
    public void setPaymentMethodTestDE() {
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setCountryCode(COUNTRYCODE.DE)
            .usePayPage()
            .setPaymentMethod(PAYMENTMETHOD.INVOICE);
        payPagePayment.configureExcludedPaymentMethods();
        
        assertEquals(INVOICETYPE.INVOICE_DE.getValue(), payPagePayment.getPaymentMethod());
    }
    
    @Test
    public void setPaymentMethodPaymentPlanTestSE() {
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .usePayPage()
            .setPaymentMethod(PAYMENTMETHOD.PAYMENTPLAN);
        payPagePayment.configureExcludedPaymentMethods();
        
        assertEquals(PAYMENTPLANTYPE.PAYMENTPLAN_SE.getValue(), payPagePayment.getPaymentMethod());
    }
    @Test
    public void excludePaymentPlanTestSE() {
        ArrayList<PAYMENTMETHOD> list = new ArrayList<PAYMENTMETHOD>();
        list.add(PAYMENTMETHOD.PAYMENTPLAN);
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .usePayPage()
            .excludePaymentMethods(list);
        
        payPagePayment.configureExcludedPaymentMethods();
        
        assertEquals(PAYMENTPLANTYPE.PAYMENTPLAN_SE.getValue(),payPagePayment.getExcludedPaymentMethods().get(0));
    }
    
    @Test
    public void setPaymentMethodPaymentPlanTestNL() {
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setCountryCode(COUNTRYCODE.NL)
            .usePayPage()
            .setPaymentMethod(PAYMENTMETHOD.PAYMENTPLAN);
        
        payPagePayment.configureExcludedPaymentMethods();
        
        assertEquals(PAYMENTPLANTYPE.PAYMENTPLAN_NL.getValue(), payPagePayment.getPaymentMethod());
    }
    
    @Test
    public void excludeCardPaymentMethodsTest() {
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .usePayPage()
                .excludeCardPaymentMethods();
        
        assertEquals(2, payPagePayment.getExcludedPaymentMethods().size());
        assertEquals(PAYMENTMETHOD.KORTCERT.getValue(), payPagePayment.getExcludedPaymentMethods().get(0));
        assertEquals(PAYMENTMETHOD.SKRILL.getValue(), payPagePayment.getExcludedPaymentMethods().get(1));
    }
    
    @Test
    public void includeTCardPaymentMethodsTest() {
        List<PAYMENTMETHOD> includedPaymentMethods = new ArrayList<PAYMENTMETHOD>();
        includedPaymentMethods.add(PAYMENTMETHOD.KORTCERT);
        includedPaymentMethods.add(PAYMENTMETHOD.SKRILL);
        
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .usePayPage()
                .includePaymentMethods(includedPaymentMethods);
        
        assertEquals(2, payPagePayment.getIncludedPaymentMethods().size());
    }
    
    @Test
    public void excludeDirectPaymentMethodsTest() {
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .usePayPage()
                .excludeDirectPaymentMethods();
        
        assertEquals(6, payPagePayment.getExcludedPaymentMethods().size());
        assertEquals(0, payPagePayment.getIncludedPaymentMethods().size());
    }
    
    @Test
    public void includePaymentMethodsTest() {
        List<PAYMENTMETHOD> includedPaymentMethods = new ArrayList<PAYMENTMETHOD>();
        includedPaymentMethods.add(PAYMENTMETHOD.KORTCERT);
        includedPaymentMethods.add(PAYMENTMETHOD.SKRILL);
        includedPaymentMethods.add(PAYMENTMETHOD.INVOICE);
        includedPaymentMethods.add(PAYMENTMETHOD.PAYMENTPLAN);
        includedPaymentMethods.add(PAYMENTMETHOD.SWEDBANK_SE);
        includedPaymentMethods.add(PAYMENTMETHOD.SHB_SE);
        includedPaymentMethods.add(PAYMENTMETHOD.SEBFTG_SE);
        includedPaymentMethods.add(PAYMENTMETHOD.SEB_SE);
        includedPaymentMethods.add(PAYMENTMETHOD.NORDEA_SE);
        
        PayPagePayment payPagePayment = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .usePayPage()
            .includePaymentMethods(includedPaymentMethods);
        
        assertEquals(14, payPagePayment.getExcludedPaymentMethods().size());
        assertEquals("SVEAINVOICESE", payPagePayment.getExcludedPaymentMethods().get(0));
        assertEquals("SVEASPLITSE", payPagePayment.getExcludedPaymentMethods().get(1));
        assertEquals("SVEAINVOICEEU_DE", payPagePayment.getExcludedPaymentMethods().get(2));
        assertEquals("SVEASPLITEU_DE", payPagePayment.getExcludedPaymentMethods().get(3));
        assertEquals("SVEAINVOICEEU_DK", payPagePayment.getExcludedPaymentMethods().get(4));
        assertEquals("PAYPAL", payPagePayment.getExcludedPaymentMethods().get(12));
        assertEquals("BANKAXESS", payPagePayment.getExcludedPaymentMethods().get(13));
    }
    
    @Test
    public void testBuildPayPagePaymentWithExcludepaymentMethods() {
        List<PAYMENTMETHOD> paymentMethods = new ArrayList<PAYMENTMETHOD>();
        paymentMethods.add(PAYMENTMETHOD.INVOICE);
        paymentMethods.add(PAYMENTMETHOD.KORTCERT);
        
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addFee(TestingTool.createExVatBasedShippingFee())
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPage()
            .excludePaymentMethods(paymentMethods)
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--{\"X-Svea-Integration-Version\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Integration-Platform\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Name\":\"2.0.2\",\"X-Svea-Integration-Company\":\"Integration package default SveaTestConfigurationProvider.\",\"X-Svea-Library-Version\":\"Java Integration Package\"}--><payment><customerrefno>33</customerrefno><currency>SEK</currency><amount>18750</amount><vat>3750</vat><returnurl>http://myurl.se</returnurl><iscompany>false</iscompany><customer><ssn>194605092222</ssn><country>SE</country></customer><orderrows><row><sku>1</sku><name>Prod</name><description>Specification</description><amount>12500</amount><vat>2500</vat><quantity>2.0</quantity><unit>st</unit></row><row><sku>33</sku><name>shipping</name><description>Specification</description><amount>6250</amount><vat>1250</vat><quantity>1.0</quantity><unit>st</unit></row><row><sku>1</sku><name>Relative</name><description>RelativeDiscount</description><amount>-12500</amount><vat>-2500</vat><quantity>1.0</quantity><unit>st</unit></row></orderrows><excludepaymentmethods><exclude>SVEAINVOICESE</exclude><exclude>SVEAINVOICEEU_SE</exclude><exclude>SVEAINVOICEEU_NO</exclude><exclude>SVEAINVOICEEU_DK</exclude><exclude>SVEAINVOICEEU_FI</exclude><exclude>SVEAINVOICEEU_NL</exclude><exclude>SVEAINVOICEEU_DE</exclude><exclude>KORTCERT</exclude></excludepaymentmethods><addinvoicefee>false</addinvoicefee></payment>";
        assertEquals(expected, xml);
    }
    
    @Test
    public void testPayPagePaymentExcludeCardPayments() {
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPage()
            .excludeCardPaymentMethods()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String paymentMethod = xml.substring(xml.indexOf("KORTCERT"), xml.indexOf("KORTCERT") + 8);
        String paymentMethod2 = xml.substring(xml.indexOf("SKRILL"), xml.indexOf("SKRILL") + 6);
        
        assertEquals(PAYMENTMETHOD.KORTCERT.getValue(), paymentMethod);
        assertEquals("SKRILL", paymentMethod2); 
    }
    
    @Test
    public void testExcludeDirectPaymentMethods() {
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(Item.individualCustomer()
                .setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPage()
            .excludeDirectPaymentMethods()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String paymentMethod = xml.substring(xml.indexOf("DBNORDEASE"), xml.indexOf("DBNORDEASE") + 10);
        assertEquals(PAYMENTMETHOD.NORDEA_SE.getValue(), paymentMethod);
    }
    
    @Test
    public void testIncludePaymentPlan() {
        List<PAYMENTMETHOD> paymentMethods = new ArrayList<PAYMENTMETHOD>();
        paymentMethods.add(PAYMENTMETHOD.PAYMENTPLAN);
        paymentMethods.add(PAYMENTMETHOD.SKRILL);
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
                .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
                .addDiscount(TestingTool.createRelativeDiscount())
                .addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
                .setOrderDate(TestingTool.DefaultTestDate)
                .setCurrency(TestingTool.DefaultTestCurrency)
                .usePayPage()
                .includePaymentMethods(paymentMethods)
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String paymentMethod = xml.substring(xml.indexOf("SVEAINVOICESE"), xml.indexOf("SVEAINVOICESE") + 13);
        
        //check to see if the first value is one of the excluded ones
        assertEquals(INVOICETYPE.INVOICESE.getValue(), paymentMethod);
    }
    
    @Test
    public void testpayPagePaymentIncludePaymentMethods() {
        List<PAYMENTMETHOD> paymentMethods = new ArrayList<PAYMENTMETHOD>();
        paymentMethods.add(PAYMENTMETHOD.KORTCERT);
        paymentMethods.add(PAYMENTMETHOD.SKRILL);
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPage()
            .includePaymentMethods(paymentMethods)
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String paymentMethod = xml.substring(xml.indexOf("SVEAINVOICESE"), xml.indexOf("SVEAINVOICESE") + 13);
        //check to see if the first value is one of the excluded ones
        assertEquals(INVOICETYPE.INVOICESE.getValue(), paymentMethod);
    }
    
    @Test
    public void testPayPagePaymentIncludePaymentMethodsEmpty() {
        PaymentForm form = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(Item.individualCustomer().setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber))
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPage()
            .includePaymentMethods()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String paymentMethod = xml.substring(xml.indexOf("SVEAINVOICESE"), xml.indexOf("SVEAINVOICESE") + 13);
        String paymentMethod2 = xml.substring(xml.indexOf("DBSWEDBANKSE"), xml.indexOf("DBSWEDBANKSE") + 12);
        
        assertEquals(INVOICETYPE.INVOICESE.getValue(), paymentMethod);
        assertEquals(PAYMENTMETHOD.SWEDBANK_SE.getValue(), paymentMethod2);
    }
}
