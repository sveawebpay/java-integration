package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;

/**
 * Tests that orders created with a mix of order and fee rows specified as exvat/incvat and vatpercent 
 * are sent to the webservice using the correct PriceIncludingVat flag.
 * 
 * Also tests that fixed discount rows specified as amount inc/exvat only are split correctly across 
 * all the order vat rates, and that the split discount rows are sent to the service using the correct
 * PriceIncludingVat flag.
 * 
 * @author Kristian Grossman-Madsen
 */
public class FixedDiscountRowsTest {

    private static CreateOrderBuilder create_mixed_exvat_and_incvat_order_and_fee_rows_order() {
    	CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
    			.addCustomerDetails( 
    					WebPayItem.individualCustomer()
    						.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
    			)
    			.setCountryCode(COUNTRYCODE.SE)
    			.setCustomerReference("33")
    			.setOrderDate(java.sql.Date.valueOf("2012-12-12"))
    			.addOrderRow(
    					WebPayItem.orderRow()
    						.setAmountExVat(60.00)
    						.setVatPercent(20)
    						.setQuantity(1D)
    						.setName("exvatRow")
				)
    			.addOrderRow(
    					WebPayItem.orderRow()
    						.setAmountIncVat(33.00)
    						.setVatPercent(10)
    						.setQuantity(1D)
    						.setName("incvatRow")
				)
				.addFee(
    					WebPayItem.invoiceFee()
							.setAmountIncVat(8.80)
							.setVatPercent(10)
							.setName("incvatInvoiceFee")
				)
				.addFee(
    					WebPayItem.shippingFee()
							.setAmountExVat(16.00)
							.setVatPercent(10)
							.setName("exvatShippingFee")
				)
		;
    	return order;
    }
        
    // order with order/fee rows mixed exvat+vat / incvat+vat should be sent with PriceIncludingVat = false
    @Test
    public void test_mixed_order_row_and_shipping_fees_only_has_priceIncludingVat_false() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_mixed_exvat_and_incvat_order_and_fee_rows_order();
        		
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(60.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(30.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(16.00, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.00, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);
    }
	  
    // same order with discount exvat should be sent with PriceIncludingVat = false but with split discount rows based on order amounts ex vat
    @Test
    public void test_mixed_order_with_fixedDiscount_as_exvat_only_has_priceIncludingVat_false() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_mixed_exvat_and_incvat_order_and_fee_rows_order();
        order.addDiscount(
    		WebPayItem.fixedDiscount()
    			.setAmountExVat(10.0)
    			//.setVatPercent(10)
            	.setDiscountId("fixedDiscount")
            	.setName("fixedDiscount: 10e")
        	)
        ;        
        
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(60.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(30.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(16.00, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.00, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);        
        // all discount rows
        // expected: fixedDiscount: 10 exvat => split across 10e *(60/60+30) @20% + 10e *(30/60+30) @10% => 6.67e @20% + 3.33e @10%
        assertEquals(-6.67, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(20, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals(-3.33, request.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(5).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat);        
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.
    }    

    // same order with discount incvat should be sent with PriceIncludingVat = false but with split discount rows based on order amounts inc vat
    @Test
    public void test_mixed_order_with_fixedDiscount_as_incvat_only_has_priceIncludingVat_false() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_mixed_exvat_and_incvat_order_and_fee_rows_order();
        order.addDiscount(
    		WebPayItem.fixedDiscount()
    			.setAmountIncVat(10.0)
    			//.setVatPercent(10)
            	.setDiscountId("fixedDiscount")
            	.setName("fixedDiscount: 10i")
        	)
        ;        
        
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(60.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(30.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(16.00, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.00, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);        
        // all discount rows
        // expected: fixedDiscount: 10 incvat => split across 10i *(72/72+33) @20% + 10i *(33/72+33) @10% => 6.8571i @20% + 3.1428i @10% =>5.71 + 2.86
        assertEquals(-5.71, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(20, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals(-2.86, request.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(5).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat);        
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.
    }     
    
    // same order with discount exvat+vat should be sent with PriceIncludingVat = false with one discount row amount based on given exvat + vat
    @Test
    public void test_mixed_order_with_fixedDiscount_as_exvat_and_vatpercent_has_priceIncludingVat_false() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_mixed_exvat_and_incvat_order_and_fee_rows_order();
        order.addDiscount(
    		WebPayItem.fixedDiscount()
    			.setAmountExVat(10.0)
    			.setVatPercent(10)
            	.setDiscountId("fixedDiscount")
            	.setName("fixedDiscount: 10e@10%")
        	)
        ;        
        
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(60.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(30.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(16.00, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.00, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);        
        // all discount rows
        // expected: fixedDiscount: 11 incvat @10% => -10e @10% 
        assertEquals(-10.00, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals( 5, request.request.CreateOrderInformation.OrderRows.size() );
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.
    }

    // same order with discount incvat+vat should be sent with PriceIncludingVat = false with one discount row amount based on given incvat + vat
    @Test
    public void test_mixed_order_with_fixedDiscount_as_incvat_and_vatpercent_has_priceIncludingVat_false() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_mixed_exvat_and_incvat_order_and_fee_rows_order();
        order.addDiscount(
    		WebPayItem.fixedDiscount()
    			.setAmountIncVat(11.0)
    			.setVatPercent(10)
            	.setDiscountId("fixedDiscount")
            	.setName("fixedDiscount: 11i@10%")
        	)
        ;        
        
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(60.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(30.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(16.00, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.00, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);        
        // all discount rows
        // expected: fixedDiscount: 11 incvat @10% => -10e @10% 
        assertEquals(-10.00, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals( 5, request.request.CreateOrderInformation.OrderRows.size() );
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.
    }

    private static CreateOrderBuilder create_only_incvat_order_and_fee_rows_order() {
    	CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
    			.addCustomerDetails( 
    					WebPayItem.individualCustomer()
    						.setNationalIdNumber(TestingTool.DefaultTestIndividualNationalIdNumber)
    			)
    			.setCountryCode(COUNTRYCODE.SE)
    			.setCustomerReference("33")
    			.setOrderDate(java.sql.Date.valueOf("2012-12-12"))
    			.addOrderRow(
    					WebPayItem.orderRow()
    						.setAmountIncVat(72.00)
    						.setVatPercent(20)
    						.setQuantity(1D)
    						.setName("incvatRow")
				)
    			.addOrderRow(
    					WebPayItem.orderRow()
    						.setAmountIncVat(33.00)
    						.setVatPercent(10)
    						.setQuantity(1D)
    						.setName("incvatRow2")
				)
				.addFee(
    					WebPayItem.invoiceFee()
							.setAmountIncVat(8.80)
							.setVatPercent(10)
							.setName("incvatInvoiceFee")
				)
				.addFee(
    					WebPayItem.shippingFee()
							.setAmountIncVat(17.60)
							.setVatPercent(10)
							.setName("incvatShippingFee")
				)
		;
    	return order;
    }

    // order with order/fee rows all having incvat should be sent with PriceIncludingVat = true
    @Test
    public void test_incvat_order_row_and_shipping_fees_only_has_priceIncludingVat_true() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_only_incvat_order_and_fee_rows_order();
        		
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(72.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(33.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(17.60, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.80, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.        
    }    
    
    // same order with discount exvat should be sent with PriceIncludingVat = true but with split discount rows based on order amounts ex vat
    @Test
    public void test_incvat_order_with_fixedDiscount_as_exvat_only_has_priceIncludingVat_true() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_only_incvat_order_and_fee_rows_order();
        order.addDiscount(
    		WebPayItem.fixedDiscount()
    			.setAmountExVat(10.0)
    			//.setVatPercent(10)
            	.setDiscountId("fixedDiscount")
            	.setName("fixedDiscount: 10e")
        	)
        ;        
        
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(72.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(33.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(17.60, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.80, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);      
        // all discount rows
        // expected: fixedDiscount: 10 exvat => split across 10e *(60/60+30) @20% + 10e *(30/60+30) @10% => 6.6666e @20% + 3.3333e @10% => 8.00i + 3.67i
        assertEquals(-8.00, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(20, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals(-3.67, request.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(5).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat);       
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.
    }  
    
    // same order with discount incvat should be sent with PriceIncludingVat = true but with split discount rows based on order amounts inc vat
    @Test
    public void test_incvat_order_with_fixedDiscount_as_incvat_only_has_priceIncludingVat_true() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_only_incvat_order_and_fee_rows_order();
        order.addDiscount(
    		WebPayItem.fixedDiscount()
    			.setAmountIncVat(10.0)
    			//.setVatPercent(10)
            	.setDiscountId("fixedDiscount")
            	.setName("fixedDiscount: 10i")
        	)
        ;        
        
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(72.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(33.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(17.60, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.80, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);      
        // all discount rows
        // expected: fixedDiscount: 10 incvat => split across 10i *(72/72+33) @20% + 10i *(33/72+33) @10% => 6.8571i @20% + 3.1428i @10%
        assertEquals(-6.86, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(20, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals(-3.14, request.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(5).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat);       
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.
    }
    
    // same order with discount exvat+vat should be sent with PriceIncludingVat = true with one discount row amount based on given exvat + vat
    @Test
    public void test_incvat_order_with_fixedDiscount_as_exvat_and_vatpercent_has_priceIncludingVat_true() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_only_incvat_order_and_fee_rows_order();
        order.addDiscount(
    		WebPayItem.fixedDiscount()
    			.setAmountExVat(10.0)
    			.setVatPercent(10)
            	.setDiscountId("fixedDiscount")
            	.setName("fixedDiscount: 10e@10%")
        	)
        ;        
        
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(72.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(33.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(17.60, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.80, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);           
        // all discount rows
        // expected: fixedDiscount: 11 incvat @10% => -10e @10% 
        assertEquals(-11.00, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals( 5, request.request.CreateOrderInformation.OrderRows.size() );
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.
    }
    
    // same order with discount incvat+vat should be sent with PriceIncludingVat = true with one discount row amount based on given incvat + vat
    @Test
    public void test_incvat_order_with_fixedDiscount_as_incvat_and_vatpercent_has_priceIncludingVat_true() {
        CreateOrderBuilder order = FixedDiscountRowsTest.create_only_incvat_order_and_fee_rows_order();
        order.addDiscount(
    		WebPayItem.fixedDiscount()
    			.setAmountIncVat(11.0)
    			.setVatPercent(10)
            	.setDiscountId("fixedDiscount")
            	.setName("fixedDiscount: 11i@10%")
        	)
        ;        
        
        SveaRequest<SveaCreateOrder> request = order.useInvoicePayment().prepareRequest();
        // all invoice fee rows        
        assertEquals(72.00, request.request.CreateOrderInformation.OrderRows.get(0).PricePerUnit, 0);
        assertEquals(20.0, request.request.CreateOrderInformation.OrderRows.get(0).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(0).PriceIncludingVat);
        assertEquals(33.00, request.request.CreateOrderInformation.OrderRows.get(1).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(1).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(1).PriceIncludingVat);        
        // all shipping fee rows
        assertEquals(17.60, request.request.CreateOrderInformation.OrderRows.get(2).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(2).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(2).PriceIncludingVat);
        // all invoice fee rows
        assertEquals(8.80, request.request.CreateOrderInformation.OrderRows.get(3).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(3).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(3).PriceIncludingVat);           
        // all discount rows
        // expected: fixedDiscount: 11 incvat @10% => -10e @10% 
        assertEquals(-11.00, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(true, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals( 5, request.request.CreateOrderInformation.OrderRows.size() );
        
        // See file IntegrationTest/WebService/Payment/FixedDiscountRowsIntegrationTest for service response tests.
    }
}
