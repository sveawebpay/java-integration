package se.sveaekonomi.webpay.integration.hosted;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;


public class HostedPaymentsRequestTest {
    
    private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = new CreateOrderBuilder();
    }
    
    @Test
    public void testDoCardPaymentRequest() throws Exception {        
        order.setTestmode();
        order.addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0));
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
        PaymentForm form = order.setCountryCode(COUNTRYCODE.SE)
        .setOrderDate("2012-12-12")
        .setClientOrderNumber("33")
        .setCurrency("SEK")
        .usePayPageCardOnly()
            .setReturnUrl("http://myurl.se")            
            .getPaymentForm();
        
        Assert.assertNotNull(form);
       }
}
