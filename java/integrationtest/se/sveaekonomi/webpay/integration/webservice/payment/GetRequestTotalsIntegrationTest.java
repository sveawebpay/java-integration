package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
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
public class GetRequestTotalsIntegrationTest {
	
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
    
    @Test
    public void test_getOrderTotals_has_same_amounts_as_service_when_order_sent_priceIncludingVat_false() {
    
        CreateOrderBuilder order = GetRequestTotalsIntegrationTest.create_only_incvat_order_and_fee_rows_order();
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
        // expected: fixedDiscount: 10 exvat => split across 10e *(60/60+30) @20% + 10e *(30/60+30) @10% => 6.6666e @20% + 3.3333e @10% => 8.00i + 3.67i
        assertEquals(-6.67, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(20, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals(-3.33, request.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(5).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat);     
       
        CreateOrderResponse response = order.useInvoicePayment().doRequest();
        assertTrue( response.isOrderAccepted());
        
        // r() is round($val, 2, PHP_ROUND_HALF_EVEN), i.e. bankers rounding
        // r(72.00*1) + r(33.00*1) + r(17.60*1) + r(8.80*1) + r(-8.00*1) + r(-3.66*1) => 72.00+33.00+17.60+8.80-8.00-3.67 => 119.73
        //$this->assertEquals( "119.73", $response->amount );     // TODO check that this is the amount in S1 invoice, vs 119.74 w/PriceIncludingVat = false
        assertEquals((Double)119.74, response.getAmount());		// jfr vs 119.73 w/PriceIncludingVat = true
        
        // verify that getRequestTotals() got the same amount as the service
        HashMap<String, Double> preview = order.useInvoicePayment().getRequestTotals();
        assertEquals(preview.get("total_incvat"), response.getAmount());
    }  

    @Test
    public void test_getOrderTotals_has_same_amounts_as_service_when_order_sent_priceIncludingVat_false2() {
    
        CreateOrderBuilder order = GetRequestTotalsIntegrationTest.create_only_incvat_order_and_fee_rows_order();
        order.addDiscount(
                WebPayItem.fixedDiscount()
                .setAmountExVat(6.67)
                .setVatPercent(20)
                .setDiscountId("fixedDiscount")
                .setName("fixedDiscount: 6.67e@20%")
            )
            .addDiscount(
                WebPayItem.fixedDiscount()
                .setAmountExVat(3.33)
                .setVatPercent(10)
                .setDiscountId("fixedDiscount")
                .setName("fixedDiscount: 3.33e@10%")        	
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
        // expected: fixedDiscount: 10 exvat => split across 10e *(60/60+30) @20% + 10e *(30/60+30) @10% => 6.6666e @20% + 3.3333e @10% => 8.00i + 3.67i
        assertEquals(-6.67, request.request.CreateOrderInformation.OrderRows.get(4).PricePerUnit, 0);
        assertEquals(20, request.request.CreateOrderInformation.OrderRows.get(4).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(4).PriceIncludingVat);        
        assertEquals(-3.33, request.request.CreateOrderInformation.OrderRows.get(5).PricePerUnit, 0);
        assertEquals(10, request.request.CreateOrderInformation.OrderRows.get(5).VatPercent, 0);
        assertEquals(false, request.request.CreateOrderInformation.OrderRows.get(5).PriceIncludingVat);     
       
        CreateOrderResponse response = order.useInvoicePayment().doRequest();
        assertTrue( response.isOrderAccepted());
        
        // r() is round($val, 2, PHP_ROUND_HALF_EVEN), i.e. bankers rounding
        // r(72.00*1) + r(33.00*1) + r(17.60*1) + r(8.80*1) + r(-8.00*1) + r(-3.66*1) => 72.00+33.00+17.60+8.80-8.00-3.67 => 119.73
        //$this->assertEquals( "119.73", $response->amount );     // TODO check that this is the amount in S1 invoice, vs 119.74 w/PriceIncludingVat = false
        assertEquals((Double)119.74, response.getAmount());		// jfr vs 119.73 w/PriceIncludingVat = true
        
        // verify that getRequestTotals() got the same amount as the service
        HashMap<String, Double> preview = order.useInvoicePayment().getRequestTotals();
        assertEquals(preview.get("total_incvat"), response.getAmount());
    }  
    
    @Test
    public void test_getOrderTotals_has_same_amounts_as_service_when_order_sent_priceIncludingVat_true() {
    
        CreateOrderBuilder order = GetRequestTotalsIntegrationTest.create_only_incvat_order_and_fee_rows_order();
        order.addDiscount(
                WebPayItem.fixedDiscount()
                .setAmountIncVat(8.00)
                .setVatPercent(20)
                .setDiscountId("fixedDiscount")
                .setName("fixedDiscount: 8.00i@20%")
            )
            .addDiscount(
                WebPayItem.fixedDiscount()
                .setAmountIncVat(3.67)
                .setVatPercent(10)
                .setDiscountId("fixedDiscount")
                .setName("fixedDiscount: 3.67i@10%")        	
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
       
        CreateOrderResponse response = order.useInvoicePayment().doRequest();
        assertTrue( response.isOrderAccepted());
        
        // r() is round($val, 2, PHP_ROUND_HALF_EVEN), i.e. bankers rounding
        // r(72.00*1) + r(33.00*1) + r(17.60*1) + r(8.80*1) + r(-8.00*1) + r(-3.66*1) => 72.00+33.00+17.60+8.80-8.00-3.67 => 119.73
        assertEquals((Double)119.73, response.getAmount());     // TODO check that this is the amount in S1 invoice, vs 119.74 w/PriceIncludingVat = false
        //assertEquals((Double)119.74, response.getAmount());		// jfr vs 119.73 w/PriceIncludingVat = true
        
        // verify that getRequestTotals() got the same amount as the service
        HashMap<String, Double> preview = order.useInvoicePayment().getRequestTotals();
        assertEquals(preview.get("total_incvat"), response.getAmount());
    }     
    
}
