package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.ExcludePayments;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.INVOICETYPE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTPLANTYPE;

public class HostedPaymentTest {

    @Test
    public void testCalculateRequestValuesNullExtraRows() {
        CreateOrderBuilder order = WebPay.createOrder() 
            .setCountryCode(COUNTRYCODE.SE)
               .setClientOrderNumber("nr22")
               .setCurrency(CURRENCY.SEK)
            .addOrderRow(Item.orderRow()
                    .setAmountExVat(4)
                    .setVatPercent(25)
                    .setQuantity(1))
            
            .addFee(Item.shippingFee())
            .addDiscount(Item.fixedDiscount())
            .addDiscount(Item.relativeDiscount());
            
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment
            .setReturnUrl("myurl")
            .calculateRequestValues();
        
        assertTrue(500L == payment.getAmount());
    }

    @Test
    public void testVatPercentAndAmountIncVatCalculation() {
        CreateOrderBuilder order = WebPay.createOrder()  
            .setCountryCode(COUNTRYCODE.SE)
            .setClientOrderNumber("nr22")
            .setCurrency(CURRENCY.SEK)
            .addOrderRow(Item.orderRow()
                    .setAmountIncVat(5)
                    .setVatPercent(25)
                    .setQuantity(1));
        
        order.setShippingFeeRows(null);
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment
            .setReturnUrl("myUrl")
            .calculateRequestValues();
        
        assertEquals(500L,payment.getAmount(), 0);
    }
    
    @Test
    public void testAmountIncVatAndvatPercentShippingFee() {
      CreateOrderBuilder order = WebPay.createOrder() 
            .setCountryCode(COUNTRYCODE.SE)
             .setClientOrderNumber("nr22")
             .setCurrency(CURRENCY.SEK)  
            .addOrderRow(Item.orderRow()
                    .setAmountIncVat(5)
                    .setVatPercent(25)
                    .setQuantity(1))
            
            .addFee(Item.shippingFee()
                    .setAmountIncVat(5)
                    .setVatPercent(25));
        
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment
            .setReturnUrl("myUrl")
            .calculateRequestValues();
        
        assertEquals(1000L,payment.getAmount(), 0);
    }
    
    @Test
    public void testAmountIncVatAndAmountExVatCalculation() {
         CreateOrderBuilder order = WebPay.createOrder()
                 .setCountryCode(COUNTRYCODE.SE)
                 .setClientOrderNumber("nr22")
                 .setCurrency(CURRENCY.SEK)
                 .addOrderRow(Item.orderRow()
                        .setAmountExVat(4)
                        .setAmountIncVat(5)
                        .setQuantity(1));
        
        order.setShippingFeeRows(null);
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment
            .setReturnUrl("myurl")
            .calculateRequestValues();
        
        assertTrue(500L == payment.getAmount());
    }
    
    @Test
    public void testCreatePaymentForm() throws Exception {
         CreateOrderBuilder order = WebPay.createOrder() 
                 .setCountryCode(COUNTRYCODE.SE)
                 .setClientOrderNumber("nr22")
                 .setCurrency(CURRENCY.SEK)
                .addOrderRow(Item.orderRow()
                        .setAmountExVat(4)
                        .setVatPercent(25)
                        .setQuantity(1))
                .addCustomerDetails(Item.companyCustomer()
                    .setNationalIdNumber("666666")
                    .setEmail("test@svea.com")
                    .setPhoneNumber("999999")
                    .setIpAddress("123.123.123.123")
                    .setStreetAddress("Gatan", "23")
                    .setCoAddress("c/o Eriksson")
                    .setZipCode("9999")
                    .setLocality("Stan")); 
        
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment.setReturnUrl("myurl");
        PaymentForm form;
        
        form = payment.getPaymentForm();
        
        Map<String, String> formHtmlFields = form.getFormHtmlFields();
        assertEquals("</form>", formHtmlFields.get("form_end_tag"));
    }
    
    @Test
    public void testExcludeInvoicesAndAllInstallmentsAllCountries() {
        FakeHostedPayment payment = new FakeHostedPayment(null);
        ExcludePayments exclude = new ExcludePayments();
        List<String> excludedPaymentMethods = payment.getExcludedPaymentMethods();
        excludedPaymentMethods.addAll(exclude.excludeInvoicesAndPaymentPlan());
        
        assertEquals(14, excludedPaymentMethods.size());
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICESE.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_SE.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_DE.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_DK.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_FI.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_NL.getValue()));
        assertTrue(excludedPaymentMethods.contains(INVOICETYPE.INVOICE_NO.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLANSE.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_SE.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_DE.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_DK.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_FI.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_NL.getValue()));
        assertTrue(excludedPaymentMethods.contains(PAYMENTPLANTYPE.PAYMENTPLAN_NO.getValue()));
    }
}
