package se.sveaekonomi.webpay.integration.webservice.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.WebPayItem;
import se.sveaekonomi.webpay.integration.config.SveaConfig;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.FixedDiscountBuilder;
import se.sveaekonomi.webpay.integration.order.row.InvoiceFeeBuilder;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.order.row.ShippingFeeBuilder;
import se.sveaekonomi.webpay.integration.response.webservice.CreateOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.DeliverOrderResponse;
import se.sveaekonomi.webpay.integration.response.webservice.PaymentPlanParamsResponse;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.DISTRIBUTIONTYPE;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class DoPaymentPlanPaymentTest {
    
    @Test
    public void testPaymentPlanRequestReturnsAcceptedResult() {
        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .doRequest();
        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();
        
        CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
        .addOrderRow(TestingTool.createPaymentPlanOrderRow())
        .addCustomerDetails(TestingTool.createIndividualCustomer())
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
        .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
        .setOrderDate(TestingTool.DefaultTestDate)
        .setCurrency(TestingTool.DefaultTestCurrency)
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .usePaymentPlanPayment(code)
        .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }
    
    @Test
    public void testDeliverPaymentPlanOrderResult() {
        long orderId = createPaymentPlanAndReturnOrderId();
        
        DeliverOrderResponse response = WebPay.deliverOrder(SveaConfig.getDefaultConfig())
        .addOrderRow(TestingTool.createPaymentPlanOrderRow())
        .setOrderId(orderId)
        .setNumberOfCreditDays(1)
        .setInvoiceDistributionType(DISTRIBUTIONTYPE.Post)
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .deliverPaymentPlanOrder()
        .doRequest();
        
        assertTrue(response.isOrderAccepted());
    }
    
    private long createPaymentPlanAndReturnOrderId() {
        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
                .setCountryCode(TestingTool.DefaultTestCountryCode)
                .doRequest();
        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();
        
        CreateOrderResponse response = WebPay.createOrder(SveaConfig.getDefaultConfig())
            .addOrderRow(TestingTool.createPaymentPlanOrderRow())
        .addCustomerDetails(TestingTool.createIndividualCustomer())
        .setCountryCode(TestingTool.DefaultTestCountryCode)
        .setCustomerReference(TestingTool.DefaultTestCustomerReferenceNumber)
        .setClientOrderNumber(TestingTool.DefaultTestClientOrderNumber)
        .setOrderDate(TestingTool.DefaultTestDate)
        .setCurrency(TestingTool.DefaultTestCurrency)
        .usePaymentPlanPayment(code)
        .doRequest();
        
        return response.orderId;
    }
    
    
    
    /// tests for sending orderRows to webservice, specified as exvat + vat in soap request
  	@Test
  	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_false() {
  		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
  			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
  		;				
  		OrderRowBuilder exvatRow = WebPayItem.orderRow()
  				.setAmountExVat(600.00)
  				.setVatPercent(20)			
  				.setQuantity(1.0)
  				.setName("exvatRow")
  		;
  		OrderRowBuilder exvatRow2 = WebPayItem.orderRow()
  			.setAmountExVat(300.00)
  			.setVatPercent(10)			
  			.setQuantity(1.0)
  			.setName("exvatRow2")
  		;		
  		
  		InvoiceFeeBuilder exvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountExVat(80.00)
  			.setVatPercent(10)
  			.setName("exvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder exvatShippingFee = WebPayItem.shippingFee()
  			.setAmountExVat(160.00)
  			.setVatPercent(10)
  			.setName("exvatShippingFee")
  		;	
  	
  		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
  			.setAmountIncVat(10.0)
  			.setDiscountId("TenCrownsOff")
  			.setName("fixedDiscount: 10 off incvat")
  		;     
  		
  		order.addOrderRow(exvatRow);
  		order.addOrderRow(exvatRow2);
  		order.addFee(exvatInvoiceFee);
  		order.addFee(exvatShippingFee);
  		order.addDiscount(fixedDiscount);		
  		
      	// get payment plan params
        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .doRequest();
        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

        CreateOrderResponse response = order.usePaymentPlanPayment(code).doRequest();
  		
  		assertTrue( response.isOrderAccepted() );
  		assertEquals( (Object)1304.00, response.amount );		
  		System.out.println( "test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_false\n"
  				+ "  Check logs that order rows were sent as exvat+vat for order row #"+response.orderId);			
  	}		

  	@Test
  	public void test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_true() {
  		CreateOrderBuilder order = WebPay.createOrder(SveaConfig.getDefaultConfig())
  			.addCustomerDetails(TestingTool.createIndividualCustomer(COUNTRYCODE.SE))
  			.setCountryCode(TestingTool.DefaultTestCountryCode)
  			.setOrderDate(new java.sql.Date(new java.util.Date().getTime()));
  		;				
  		OrderRowBuilder incvatRow = WebPayItem.orderRow()
  				.setAmountIncVat(720.00)
  				.setVatPercent(20)			
  				.setQuantity(1.0)
  				.setName("incvatRow")
  		;
  		OrderRowBuilder incvatRow2 = WebPayItem.orderRow()
  			.setAmountIncVat(330.00)
  			.setVatPercent(10)			
  			.setQuantity(1.0)
  			.setName("incvatRow2")
  		;		
  		
  		InvoiceFeeBuilder incvatInvoiceFee = WebPayItem.invoiceFee()
  			.setAmountIncVat(88.00)
  			.setVatPercent(10)
  			.setName("incvatInvoiceFee")
  		;
  		
  		ShippingFeeBuilder incvatShippingFee = WebPayItem.shippingFee()
  			.setAmountIncVat(172.00)
  			.setVatPercent(10)
  			.setName("incvatShippingFee")
  		;	
  	
  		FixedDiscountBuilder fixedDiscount = WebPayItem.fixedDiscount()
  			.setAmountIncVat(10.0)
  			.setDiscountId("TenCrownsOff")
  			.setName("fixedDiscount: 10 off incvat")
  		;     
  		
  		order.addOrderRow(incvatRow);
  		order.addOrderRow(incvatRow2);
  		order.addFee(incvatInvoiceFee);
  		order.addFee(incvatShippingFee);
  		order.addDiscount(fixedDiscount);		

      	// get payment plan params
        PaymentPlanParamsResponse paymentPlanParam = WebPay.getPaymentPlanParams(SveaConfig.getDefaultConfig())
            .setCountryCode(TestingTool.DefaultTestCountryCode)
            .doRequest();
        String code = paymentPlanParam.getCampaignCodes().get(0).getCampaignCode();

        CreateOrderResponse response = order.usePaymentPlanPayment(code).doRequest();
  		
  		assertTrue( response.isOrderAccepted() );
  		assertEquals( (Object)1300.00, response.amount );		
  		System.out.println( "test_fixedDiscount_amount_with_specified_as_incvat_and_calculated_vat_rate_order_sent_with_PriceIncludingVat_true\n"
  				+ "  Check logs that order rows were sent as incvat+vat for order row #"+response.orderId);			
  	}		    
    
}
