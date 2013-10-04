package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DirectPaymentTest {

    @Test
    public void testConfigureExcludedPaymentMethodsSe() {
        List<String> excluded  = WebPay.createOrder() 
                .setCountryCode(COUNTRYCODE.SE)
                .usePayPageDirectBankOnly()
                .configureExcludedPaymentMethods()
                .getExcludedPaymentMethods();
        
        assertEquals(18, excluded.size());
    }
    
    @Test
    public void testConfigureExcludedPaymentMethodsNo() {
        List<String> excluded  = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.NO)
                .usePayPageDirectBankOnly()
                .configureExcludedPaymentMethods()
                .getExcludedPaymentMethods();
        
        assertEquals(22, excluded.size());
    }
    
    @Test
    public void testBuildDirectBankPayment() {
        PaymentForm form = WebPay.createOrder()
            .addOrderRow(TestingTool.createOrderRow())
            .addFee(Item.shippingFee()
                .setShippingId("33")
                .setName("shipping")
                .setDescription("Specification")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0))
            .addFee(Item.invoiceFee()
                .setName("Svea fee")
                .setDescription("Fee for invoice")
                .setAmountExVat(50)
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0))
            .addDiscount(Item.relativeDiscount()
                .setDiscountId("1")
                .setName("Relative")
                .setDescription("RelativeDiscount")
                .setUnit("st")
                .setDiscountPercent(50))
            .addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"))
            .setCountryCode(COUNTRYCODE.SE)
                .setOrderDate("2012-12-12")
                .setClientOrderNumber("33")
                .setCurrency(CURRENCY.SEK)
                .usePayPageDirectBankOnly()
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
        
        String base64Payment = form.getXmlMessageBase64();
        String html = Base64Util.decodeBase64String(base64Payment);
        String amount = html.substring(html.indexOf("<amount>") + 8, html.indexOf("</amount>"));
        
        assertEquals("18750", amount);
    }
    
    @Test
    public void testBuildDirectBankPaymentNotSE() {
        PaymentForm form = WebPay.createOrder()
            .addOrderRow(TestingTool.createOrderRow())
            .addFee(Item.shippingFee()
                 .setShippingId("33")
                 .setName("shipping")
                 .setDescription("Specification")
                 .setAmountExVat(50)
                 .setUnit("st")
                 .setVatPercent(25)
                 .setDiscountPercent(0))
            .addFee(Item.invoiceFee()
                 .setName("Svea fee")
                 .setDescription("Fee for invoice")
                 .setAmountExVat(50)
                 .setUnit("st")
                 .setVatPercent(25)
                 .setDiscountPercent(0))
            .addDiscount(Item.relativeDiscount()
                 .setDiscountId("1")
                 .setName("Relative")
                 .setDescription("RelativeDiscount")
                 .setUnit("st")
                 .setDiscountPercent(50))
            .addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"))
            .setCountryCode(COUNTRYCODE.DE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .usePayPageDirectBankOnly()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String base64Payment = form.getXmlMessageBase64();
        String html = Base64Util.decodeBase64String(base64Payment);
        String amount = html.substring(html.indexOf("<amount>") + 8, html.indexOf("</amount>"));
        
        assertEquals("18750", amount);
    }
}
