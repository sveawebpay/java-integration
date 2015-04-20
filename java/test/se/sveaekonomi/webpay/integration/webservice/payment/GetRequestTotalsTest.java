package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

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
public class GetRequestTotalsTest {

	@Test
    public void test_get_invoice_total_amount_before_createorder() {
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
    						.setAmountIncVat(19.60)
    						.setVatPercent(25)
    						.setQuantity(100D)
				)
				.addFee(
    					WebPayItem.invoiceFee()
							.setAmountIncVat(29.00)
							.setVatPercent(25)
				)
				.addDiscount(
    					WebPayItem.fixedDiscount()
							.setAmountIncVat(294.00)
				)
		;
        HashMap<String, Double> total = order.useInvoicePayment().getRequestTotals();

        assertEquals( (Double)1695.0, total.get("total_incvat") );
        assertEquals( (Double)1356.0, total.get("total_exvat") );
        assertEquals( (Double)339.0, total.get("total_vat") );
    }
	
	@Test
    public void test_get_invoice_total_amount_before_createorder_creates_discount_rows_using_incvat_and_vatpercent() {
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
    						.setAmountIncVat(19.60)
    						.setVatPercent(25)
    						.setQuantity(100D)
				)
				.addFee(
    					WebPayItem.invoiceFee()
							.setAmountIncVat(29.00)
							.setVatPercent(25)
				)
				.addDiscount(
    					WebPayItem.fixedDiscount()
							.setAmountIncVat(294.00)
				)
		;
        HashMap<String, Double> total = order.useInvoicePayment().getRequestTotals();

        assertEquals( (Double)1695.0, total.get("total_incvat") );
        assertEquals( (Double)1356.0, total.get("total_exvat") );
        assertEquals( (Double)339.0, total.get("total_vat") );
    }
}
