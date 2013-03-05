package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;


public class PaymentMethodTest {
    
    @Test
    public void testPayPagePaymentWithSetPaymentMethod() throws Exception {
        
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
        .addCustomerDetails(Item.individualCustomer()
             .setNationalIdNumber(194605092222L))
        
            .setCountryCode(COUNTRYCODE.SE)
            .setClientOrderNumber("33")
            .setOrderDate("2012-12-12")
            .setCurrency("SEK")
            .usePaymentMethod("KORTCERT")
            .setReturnUrl("http://myurl.se")                 
            .getPaymentForm();

        String base64Payment = form.getXmlMessageBase64();        
        String html = Base64Util.decodeBase64String(base64Payment);
        String amount = html.substring(html.indexOf("<paymentmethod>") + 15, html.indexOf("</paymentmethod>"));
        assertEquals("KORTCERT", amount);
    }
    
}
