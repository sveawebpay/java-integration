package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class CardPaymentTest {

    private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = WebPay.createOrder()
                .setValidator(new VoidValidator());
    }
    
    @Test
    public void testConfigureExcludedPaymentMethodsSe() {
        List<String> excluded = order
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .usePayPageCardOnly()
                .configureExcludedPaymentMethods()
                .getExcludedPaymentMethods();
        
        assertEquals(21, excluded.size());
    }
    
    @Test
    public void testBuildCardPayment() {
        PaymentForm form = order.addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
            .addFee(TestingTool.createExVatBasedShippingFee())
            .addDiscount(TestingTool.createRelativeDiscount())
            
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPageCardOnly()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String amount = xml.substring(xml.indexOf("<amount>") + 8, xml.indexOf("</amount>"));
        String vat = xml.substring(xml.indexOf("<vat>") + 5, xml.indexOf("</vat"));
        
        assertEquals("18750", amount);
        assertEquals("3750", vat);
    }
    
    @Test
    public void testBuildCardPaymentDE() {
        PaymentForm form = order.addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
            .addFee(TestingTool.createExVatBasedShippingFee())
            .addDiscount(TestingTool.createRelativeDiscount())
            .setCountryCode(COUNTRYCODE.DE)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPageCardOnly()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String amount = xml.substring(xml.indexOf("<amount>") + 8, xml.indexOf("</amount>"));
        String vat = xml.substring(xml.indexOf("<vat>") + 5, xml.indexOf("</vat"));
        
        assertEquals("18750", amount);
        assertEquals("3750", vat);
    }
    
    @Test
    public void testSetAuthorization() {
        PaymentForm form = WebPay.createOrder()
            .addOrderRow(TestingTool.createExVatBasedOrderRow("1"))
            .addFee(TestingTool.createExVatBasedShippingFee())
            .addFee(TestingTool.createExVatBasedInvoiceFee())
            .addDiscount(TestingTool.createRelativeDiscount())
            .addCustomerDetails(TestingTool.createMiniCompanyCustomer())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .setOrderDate(TestingTool.DefaultTestDate)
            .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
            .setCurrency(TestingTool.DefaultTestCurrency)
            .usePayPageCardOnly()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        assertEquals("1130", form.getMerchantId());
        assertEquals("8a9cece566e808da63c6f07ff415ff9e127909d000d259aba24daa2fed6d9e3f8b0b62e8ad1fa91c7d7cd6fc3352deaae66cdb533123edf127ad7d1f4c77e7a3", form.getSecretWord());
    }
}
