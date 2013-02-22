package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;

public class DirectPaymentTest {
    
    private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = new CreateOrderBuilder();
    }    
    
    @Test
    public void testConfigureExcludedPaymentMethodsSe() {
        order.setCountryCode(COUNTRYCODE.SE);
        DirectPayment payment = new DirectPayment(order);
        
        payment.configureExcludedPaymentMethods();
        List<PAYMENTMETHOD> excluded = payment.getExcludedPaymentMethods();
        
        assertEquals(17, excluded.size());
    }
    
    @Test
    public void testBuildDirectBankPayment() throws Exception {
         order.addOrderRow(Item.orderRow()
                .setAmountExVat(100.00)
                .setArticleNumber("1")
                .setQuantity(2)
                .setUnit("st")
                .setDescription("Specification")
                .setVatPercent(25)
                .setDiscountPercent(0)
                .setName("Prod"));
         order.addFee(Item.shippingFee()
                 .setShippingId("33")
                 .setName("shipping")
                 .setDescription("Specification")
                 .setAmountExVat(50)
                 .setUnit("st")
                 .setVatPercent(25)
                 .setDiscountPercent(0));
         order.addFee(Item.invoiceFee()
                 .setName("Svea fee")
                 .setDescription("Fee for invoice")
                 .setAmountExVat(50)
                 .setUnit("st")
                 .setVatPercent(25)
                 .setDiscountPercent(0));
         order.addDiscount(Item.relativeDiscount()
                 .setDiscountId("1")
                 .setName("Relative")
                 .setDescription("RelativeDiscount")
                 .setUnit("st")               
                 .setDiscountPercent(50));        
         order.addCustomerDetails(Item.companyCustomer()
                .setVatNumber("2345234")
                .setCompanyName("TestCompagniet"));
         PaymentForm form =   order.setCountryCode(COUNTRYCODE.SE)
                .setOrderDate("2012-12-12")
                .setClientOrderNumber("33")
                .setCurrency("SEK")
                .usePayPageDirectBankOnly()
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
        
        String base64Payment = form.getMessageBase64();        
        String html = Base64Util.decodeBase64String(base64Payment);
        String amount = html.substring(html.indexOf("<amount>") + 8, html.indexOf("</amount>"));
        assertEquals("18750", amount);
    }
}
