package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.ExcludePayments;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

public class HostedPaymentTest {

    @Test
    public void testCalculateRequestValuesNullExtraRows() throws Exception {
    	CreateOrderBuilder order = new CreateOrderBuilder();
    	order = WebPay.createOrder()
    		.setTestmode()
            .addOrderRow(Item.orderRow()
            		.setAmountExVat(4)
                    .setVatPercent(25)
                    .setQuantity(1));            
            
        order.setShippingFeeRows(null);
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        HostedPayment payment = new FakeHostedPayment(order);
        payment.calculateRequestValues();
        
        assertTrue(500L == payment.getAmount());
    }

    @Test
    public void testVatPercentAndAmountIncVatCalculation() {        
        CreateOrderBuilder order = new CreateOrderBuilder();
        order = WebPay.createOrder()
        		.setTestmode()
                .addOrderRow(Item.orderRow()
                		.setAmountExVat(4)
                        .setVatPercent(25)
                        .setQuantity(1));            
        
        order.setShippingFeeRows(null);
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        HostedPayment payment = new FakeHostedPayment(order);
        payment.calculateRequestValues();
        
        assertTrue(500L == payment.getAmount());
    }
    
    @Test
    public void testAmountIncVatAndAmountExVatCalculation() {
    	CreateOrderBuilder order = new CreateOrderBuilder();
        order = WebPay.createOrder()
        		.setTestmode()
                .addOrderRow(Item.orderRow()
                		.setAmountExVat(4)
                        .setVatPercent(25)
                        .setQuantity(1)); 
        /*ArrayList<OrderRowBuilder> rows = new ArrayList<OrderRowBuilder>();
        
        rows.add(row);
        CreateOrderBuilder order = new CreateOrderBuilder();
        
        order.addOrderRow(row);*/
        order.setShippingFeeRows(null);
        order.setFixedDiscountRows(null);
        order.setRelativeDiscountRows(null);
        HostedPayment payment = new FakeHostedPayment(order);
        payment.calculateRequestValues();
        
        assertTrue(500L == payment.getAmount());
    }
    
    @Test
    public void testCreatePaymentForm() throws Exception {
    	CreateOrderBuilder order = new CreateOrderBuilder();
        order = WebPay.createOrder()
        		.setTestmode()
                .addOrderRow(Item.orderRow()
                		.setAmountExVat(4)
                        .setVatPercent(25)
                        .setQuantity(1)); 
      /*  ArrayList<OrderRowBuilder> rows = new ArrayList<OrderRowBuilder>();
        rows.add(row);
        CreateOrderBuilder order = new CreateOrderBuilder();

        order.addOrderRow(row);*/
        HostedPayment payment = new FakeHostedPayment(order);
        PaymentForm form;
        
        try {
            form = payment.getPaymentForm();
        } catch (Exception e) {
            throw e;
        }
        
        Map<String, String> formHtmlFields = form.getFormHtmlFields();
        
        assertTrue(formHtmlFields.get("form_end_tag").equals("</form>"));
    }

    @Test
    public void testExcludeInvoicesAndInstallmentsSe() {
        HostedPayment payment = new FakeHostedPayment(null);
        ExcludePayments exclude = new ExcludePayments();       
        List<PAYMENTMETHOD> excludedPaymentMethods = payment.getExcludedPaymentMethods();
        excludedPaymentMethods.addAll(exclude.excludeInvoicesAndPaymentPlan());
        
        assertEquals(14, excludedPaymentMethods.size());
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEAINVOICESE));
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEASPLITSE));
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEAINVOICEEU_SE));
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEASPLITEU_SE));
    }

    @Test
    public void testExcludeInvoicesAndInstallmentsDe() {
        HostedPayment payment = new FakeHostedPayment(null);
        
        ExcludePayments exclude = new ExcludePayments();       
        List<PAYMENTMETHOD> excludedPaymentMethods = payment.getExcludedPaymentMethods();
        excludedPaymentMethods.addAll(exclude.excludeInvoicesAndPaymentPlan());
        
        assertEquals(14, excludedPaymentMethods.size());
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEAINVOICEEU_DE));
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEASPLITEU_DE));
    }

    @Test
    public void testExcludeInvoicesAndInstallmentsDk() {
        HostedPayment payment = new FakeHostedPayment(null);
        
        ExcludePayments exclude = new ExcludePayments();       
        List<PAYMENTMETHOD> excludedPaymentMethods = payment.getExcludedPaymentMethods();
        excludedPaymentMethods.addAll(exclude.excludeInvoicesAndPaymentPlan());
        
        assertEquals(14, excludedPaymentMethods.size());
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEAINVOICEEU_DK));
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEASPLITEU_DK));
    }

    @Test
    public void testExcludeInvoicesAndInstallmentsFi() {
        HostedPayment payment = new FakeHostedPayment(null);
        
        ExcludePayments exclude = new ExcludePayments();       
        List<PAYMENTMETHOD> excludedPaymentMethods = payment.getExcludedPaymentMethods();
        excludedPaymentMethods.addAll(exclude.excludeInvoicesAndPaymentPlan());
        
        assertEquals(14, excludedPaymentMethods.size());
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEAINVOICEEU_FI));
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEASPLITEU_FI));
    }

    @Test
    public void testExcludeInvoicesAndInstallmentsNl() {
        HostedPayment payment = new FakeHostedPayment(null);
        
        ExcludePayments exclude = new ExcludePayments();       
        List<PAYMENTMETHOD> excludedPaymentMethods = payment.getExcludedPaymentMethods();
        excludedPaymentMethods.addAll(exclude.excludeInvoicesAndPaymentPlan());
        
        assertEquals(14, excludedPaymentMethods.size());
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEAINVOICEEU_NL));
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEASPLITEU_NL));
    }

    @Test
    public void testExcludeInvoicesAndInstallmentsNo() {
        HostedPayment payment = new FakeHostedPayment(null);
        
        ExcludePayments exclude = new ExcludePayments();       
        List<PAYMENTMETHOD> excludedPaymentMethods = payment.getExcludedPaymentMethods();
        excludedPaymentMethods.addAll(exclude.excludeInvoicesAndPaymentPlan());
        
        assertEquals(14, excludedPaymentMethods.size());
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEAINVOICEEU_NO));
        assertTrue(excludedPaymentMethods.contains(PAYMENTMETHOD.SVEASPLITEU_NO));
    }
}
