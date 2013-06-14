package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.xml.bind.ValidationException;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;

public class DirectPaymentTest {
    
    @Before
    public void setUp() {
     
    }
    
    @Test
    public void testConfigureExcludedPaymentMethodsSe() throws ValidationException {
    	List<String> excluded  = WebPay.createOrder() 
    			.setCountryCode(COUNTRYCODE.SE)
    			.usePayPageDirectBankOnly()
    			.configureExcludedPaymentMethods()
    			.getExcludedPaymentMethods();
        
        assertEquals(18, excluded.size());
    }
    @Test
    public void testConfigureExcludedPaymentMethodsNo() throws ValidationException {
    	List<String> excluded  = WebPay.createOrder() 
    			.setCountryCode(COUNTRYCODE.NO)
    			.usePayPageDirectBankOnly()
    			.configureExcludedPaymentMethods()
    			.getExcludedPaymentMethods();
        
        assertEquals(22, excluded.size());
    }
    
    @Test
    public void testBuildDirectBankPayment() throws Exception {
    	PaymentForm form = WebPay.createOrder()
    	.addOrderRow(Item.orderRow()
                .setAmountExVat(100.00)
                .setArticleNumber("1")
                .setQuantity(2)
                .setUnit("st")
                .setDescription("Specification")
                .setVatPercent(25)
                .setDiscountPercent(0)
                .setName("Prod"))
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
    public void testBuildDirectBankPaymentNotSE() throws Exception {
    	PaymentForm form = WebPay.createOrder()
    	.addOrderRow(Item.orderRow()
                .setAmountExVat(100.00)
                .setArticleNumber("1")
                .setQuantity(2)
                .setUnit("st")
                .setDescription("Specification")
                .setVatPercent(25)
                .setDiscountPercent(0)
                .setName("Prod"))
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
